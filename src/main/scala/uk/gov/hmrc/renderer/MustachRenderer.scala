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

import org.fusesource.scalate.{Template, TemplateEngine}
import play.twirl.api.Html
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.ws.WSGet
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Await
import scala.concurrent.duration._

class MustacheRenderer(override val connection: WSGet, override val templateServiceAddress: String) extends MustacheRendererTrait

trait MustacheRendererTrait {

  val connection: WSGet

  val templateServiceAddress: String

  lazy val mustacheTemplateString: String = getTemplate

  protected def getTemplate: String = {
    implicit val hc = HeaderCarrier()
    Await.result[String](connection.doGet(templateServiceAddress).map(_.body), 10 seconds)
  }

  private lazy val templateEngine = new TemplateEngine()

  lazy val mustacheTemplate: Template = templateEngine.compileMoustache(mustacheTemplateString)

  def parseTemplate(title: Option[String],
                    bodyClasses: Option[String],
                    head: Html,
                    bodyEnd: Html,
                    insideHeader: Html,
                    afterHeader: Html,
                    footerTop: Html,
                    footerLinks: Option[Html],
                    nav: Boolean = false,
                    content: Html): Html = {

    val attributes = Map[String, Any](
      "pageTitle" -> title,
      "head" -> head.body,
      "bodyClasses" -> bodyClasses.getOrElse(""),
      "bodyEnd" -> bodyEnd.body,
      "nav" -> nav,
      "insideHeader" -> insideHeader.body,
      "afterHeader" -> afterHeader.body,
      "content" -> content.body,
      "footerTop" -> footerTop.body,
      "footerLinks" -> footerLinks.map(_.body).getOrElse("")
    )

    Html(templateEngine.layout("outPut.ssp", mustacheTemplate, attributes))

  }
}
