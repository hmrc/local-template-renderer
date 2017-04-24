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

import org.fusesource.scalate.Template
import org.scalatest.{FlatSpec, FunSuite, Matchers}
import play.twirl.api.Html
import uk.gov.hmrc.play.http.ws.WSGet

/**
  * Created by mo on 24/04/2017.
  */
class MustacheRendererTraitTest extends FlatSpec with Matchers {



  "MustacheRenderer" should "render template" in new Setup {
    val expectedOutputHtml = Html(
      """
        |<head>
        |    <title>
        |        first
        |    </title>
        |
        |    head
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
        |</html>
      """.stripMargin
    )

    val result = mustacheRenderer.parseTemplate(
      Some("first"),
      Some("classes"),
      Html("head"),
      Html("End of body"),
      Html("insideStory"),
      Html("<div>AfterParty</div>"),
      Html("Top footer"),
      Some(Html("Footer Links")),
      true,
      Html("<p>Some Content</p>")
    )

    result should be (expectedOutputHtml)

  }

}

trait Setup {
  val mustacheRenderer = new MustacheRendererTrait {
    override lazy val templateServiceAddress: String = ???
    override lazy val connection: WSGet = ???
    override val mustacheTemplateString =
      """
        |<head>
        |    <title>
        |        {{ pageTitle }}
        |    </title>
        |
        |    {{{ head }}}
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
        |</html>
      """.stripMargin
  }
}
