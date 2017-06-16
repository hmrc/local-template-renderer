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

import akka.actor.ActorSystem
import org.fusesource.scalate.Template
import org.scalatest.{FlatSpec, Matchers}
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.diff.{DefaultNodeMatcher, Diff, ElementSelectors}
import play.twirl.api.Html
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.ws.WSGet
import uk.gov.hmrc.play.test.WithFakeApplication

import scala.xml.{Elem, XML}
/**
  * Created by mo on 24/04/2017.
  */
class MustacheRendererTraitTest extends FlatSpec with Matchers with WithFakeApplication {


  "MustacheRenderer" should "render template" in new Setup {
    val expectedOutputHtml =
      """<html>
        |<head>
        |<title>
        |first
        |</title>
        |
        |head
        |</head>
        |
        |<body class="classes">
        |
        |<header role="banner" id="global-header" class="with-proposition">
        |    <div>
        |
        |        insideStory
        |
        |    </div>
        |</header>
        |
        |
        |<div>AfterParty</div>
        |
        |<p>Some Content</p>
        |
        |<footer>
        |
        |    <div>
        |        Top footer
        |        <div>
        |            Footer Links
        |        </div>
        |    </div>
        |</footer>
        |
        |End of body
        |</body>
        |</html>""".stripMargin

    val result = mustacheRenderer.parseTemplate(
      Html("<p>Some Content</p>"),
      Map(
        "pageTitle" -> "first",
        "head" -> Html("head"),
        "bodyClasses" -> "classes",
        "bodyEnd" -> Html("End of body"),
        "nav" -> true,
        "insideHeader" -> Html("insideStory"),
        "afterHeader" ->  Html("<div>AfterParty</div>"),
        "footerTop" -> Html("Top footer"),
        "footerLinks" -> Some(Html("Footer Links"))
      )
    )

    val diff = createDiff(expectedOutputHtml, result.toString)
    println(diff.getDifferences)
    diff.hasDifferences shouldBe false
  }

  it should "render a a template using template logic" in new Setup {
    val expectedOutputHtml =
      """
        |<html>
        |<head>
        |<title>
        |GOV.UK - The best place to find government services and information
        |</title>
        |
        |head
        |</head>
        |
        |<body class="classes">
        |
        |<header role="banner" id="global-header" class="with-proposition">
        |    <div>
        |
        |        insideStory
        |
        |    </div>
        |</header>
        |
        |
        |<div>AfterParty</div>
        |
        |<p>Some Content</p>
        |
        |<footer>
        |
        |    <div>
        |        Top footer
        |        <div>
        |            Footer Links
        |        </div>
        |    </div>
        |</footer>
        |
        |End of body
        |</body>
        |</html>""".stripMargin


    val result = mustacheRenderer.parseTemplate(
      Html("<p>Some Content</p>"),
      Map(
        "head" -> Html("head"),
        "bodyClasses" -> "classes",
        "bodyEnd" -> Html("End of body"),
        "nav" -> true,
        "insideHeader" -> Html("insideStory"),
        "afterHeader" ->  Html("<div>AfterParty</div>"),
        "footerTop" -> Html("Top footer"),
        "footerLinks" -> Some(Html("Footer Links"))
      )
    )

    val diff = createDiff(expectedOutputHtml, result.toString)
    println(diff.getControlSource)
    diff.hasDifferences shouldBe false
  }

}

trait Setup {
  val mustacheRenderer = new MustacheRendererTrait {
    override lazy val akkaSystem: ActorSystem = ???
    override lazy val templateServiceAddress: String = ???
    override lazy val connection: WSGet = ???
    override def getTemplate: String = """<html>
        |<head>
        |<title>
        |{{#pageTitle}}
        |{{ pageTitle }}
        |{{/pageTitle}}
        |{{^pageTitle}}
        |GOV.UK - The best place to find government services and information
        |{{/pageTitle}}
        |</title>
        |
        |{{{ head }}}
        |</head>
        |
        |<body class="{{{ bodyClasses }}}">
        |
        |<header role="banner" id="global-header" class="{{#nav}}with-proposition{{/nav}}">
        |    <div>
        |
        |        {{{ insideHeader }}}
        |
        |    </div>
        |</header>
        |
        |
        |{{{ afterHeader }}}
        |
        |{{{ content }}}
        |
        |<footer>
        |
        |    <div>
        |        {{{ footerTop }}}
        |        <div>
        |            {{{ footerLinks }}}
        |        </div>
        |    </div>
        |</footer>
        |
        |{{{ bodyEnd }}}
        |</body>
        |</html>""".stripMargin
  }


  def createDiff(expectedHtml: String, resultHtml: String): Diff = {
    DiffBuilder.compare(expectedHtml)
      .withTest(resultHtml)
      .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
      .build()
  }

  def xhtmlFromString(htmlString: String): Elem = XML.loadString(htmlString)

  implicit val hc: HeaderCarrier = HeaderCarrier()
}
