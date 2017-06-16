/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.renderer

import akka.actor.{ActorSystem, Cancellable}
import org.fusesource.scalate.{Template, TemplateEngine}
import play.libs.Akka
import play.twirl.api.Html
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.ws.WSGet
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class MustacheRenderer(override val connection: WSGet, override val templateServiceAddress: String) extends MustacheRendererTrait

trait MustacheRendererTrait {

  lazy val akkaSystem: ActorSystem = Akka.system()

  val connection: WSGet

  val templateServiceAddress: String

  protected def getTemplate: String = {
    implicit val hc = HeaderCarrier()
    Await.result[String](connection.doGet(templateServiceAddress).map(_.body), 10 seconds)
  }

  protected lazy val templateEngine = new TemplateEngine()

  var mustacheTemplate: Template = _

  updateTemplate(getTemplate)

  //app.injector.instanceOf[ApplicationLifecycle].addStopHook(() => Future(cancellable.cancel()))
  def scheduleGrabbingTemplate()(implicit ec: ExecutionContext): Cancellable = {
    akkaSystem.scheduler.schedule(10 milliseconds, 10 minutes) {
      updateTemplate(getTemplate)
    }
  }

  private def updateTemplate(mustacheTemplateString: String) = {
    mustacheTemplate = templateEngine.compileMoustache(mustacheTemplateString)
  }

  def parseTemplate(content: Html, extraArgs: Map[String, Any])(implicit hc: HeaderCarrier): Html = {

    val attributes: Map[String, Any] = Map(
      "article" -> content.body
    ) ++ extraArgs

    Html(templateEngine.layout("outPut.ssp", mustacheTemplate, attributes))

  }
}
