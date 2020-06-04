/*
 * Copyright 2020 HM Revenue & Customs
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

import java.io.{StringReader, StringWriter}
import java.util.concurrent.TimeUnit

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import play.api.i18n.Messages
import play.twirl.api.Html

import scala.collection.JavaConversions._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

trait TemplateRenderer {

  def fetchTemplate(path: String): Future[String]
  def templateServiceBaseUrl: String
  def refreshAfter: Duration

  val expireAfter: Duration = 7 days
  val maximumEntries: Int = 100

  protected val mustacheFactory: DefaultMustacheFactory = {
    val mf = new DefaultMustacheFactory()
    mf.setObjectHandler(new com.twitter.mustache.ScalaObjectHandler)
    mf
  }

  lazy val cache: LoadingCache[String, Mustache] =
    CacheBuilder.newBuilder()
      .maximumSize(maximumEntries)
      .refreshAfterWrite(refreshAfter.toMillis, TimeUnit.MILLISECONDS)
      .expireAfterWrite(expireAfter.toMillis, TimeUnit.MILLISECONDS)
      .build(new CacheLoader[String,Mustache] {
        override def load(path: String): Mustache = {
          val reader = new StringReader(Await.result[String](fetchTemplate(templateServiceBaseUrl + path), 10 seconds))
          mustacheFactory.compile(reader, "template")
        }
      })

  private def renderTemplate(path: String)(content: Html, extraArgs: Map[String, Any])(implicit messages: Messages): Html = {

    val isWelsh = messages.lang.code.take(2)=="cy"

    val attributes: java.util.Map[String, Any] = Map("article" -> content.body, "isWelsh" -> isWelsh) ++ extraArgs
    val m: Mustache = cache.get(path)

    val sw = new StringWriter()
    m.execute(sw, attributes)
    sw.flush()

    Html(sw.toString)
  }

  def renderDefaultTemplate(path: String, content: Html, extraArgs: Map[String, Any])(implicit messages: Messages): Html =
    renderTemplate(path)(content, extraArgs)(messages)
}
