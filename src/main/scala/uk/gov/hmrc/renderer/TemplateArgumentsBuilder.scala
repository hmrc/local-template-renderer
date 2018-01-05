/*
 * Copyright 2018 HM Revenue & Customs
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

import play.twirl.api.Html


object TemplateArgumentsBuilder {


  case class CssLinkElement(url: String, ieVersionCondition: Option[String] = None, isPrint: Boolean = false)

  sealed trait TemplateComponent

  sealed trait StyleComponent extends TemplateComponent
  case class AccountMenuStyleComponent(
    langUrls: Option[(String, String)] = None,
    signoutUrl: Option[String] = None,
    activeTab: Option[ActiveTab] = None,
    hideAccountMenu: Boolean = false
  ) extends StyleComponent
  case class TraditionalStyleComponent(signoutUrl: Option[String] = None) extends StyleComponent

  case class CssLinksComponent(elements: CssLinkElement*) extends TemplateComponent
  case class ScriptsComponent(urls: String*) extends TemplateComponent
  case class PageTitleComponent(title: String) extends TemplateComponent
  case class NavTitleComponent(title: String) extends TemplateComponent
  case class InlineScriptComponent(script: String) extends TemplateComponent
  case class SsoUrlComponent(url: String) extends TemplateComponent
  case class GetHelpFormComponent(html: Html) extends TemplateComponent
  case class BackLinkUrlComponent(
    url: String,
    text: Option[String]
  ) extends TemplateComponent
  case class MainContentHeaderComponent(html: Html) extends TemplateComponent
  case class ActingAttorneyBannerComponent(html: Html) extends TemplateComponent
  case class UserPropertiesComponent(isGovernmentGateway: Boolean, isVerify: Boolean, isSa: Boolean) extends TemplateComponent
  case class BetaBannerComponent(feedbackIdentifier: String) extends TemplateComponent
  case class GoogleAnalyticsComponent(
    trackingId: String,
    cookieDomain: Option[String],
    arguments: (String,Option[String])*
  ) extends TemplateComponent
  case class FullWidthBannerComponent(
    title: String,
    text: String,
    linkUrl: Option[String],
    dismissText: Option[String],
    gaAction: Option[String]
  ) extends TemplateComponent

  def apply(components: Option[TemplateComponent]*): Map[String, Any] = {

    def addingAccountMenuStyle(arguments: Map[String,Any], style: AccountMenuStyleComponent) = {
      arguments ++ Map(
        "hideAccountMenu"        -> style.hideAccountMenu,
        "showOrganisationLogo"   -> false,
        "showPropositionLinks"   -> style.langUrls.isDefined,
        "activeTabHome"          -> (style.activeTab == Some(ActiveTabHome)),
        "activeTabMessages"      -> (style.activeTab == Some(ActiveTabMessages)),
        "activeTabCheckProgress" -> (style.activeTab == Some(ActiveTabCheckProgress)),
        "activeTabYourAccount"   -> (style.activeTab == Some(ActiveTabYourAccount)),
        "signOutUrl"             -> style.signoutUrl,
        "langSelector" -> {
          style.langUrls match {
            case Some((enUrl, cyUrl)) => Map("enUrl" -> enUrl, "cyUrl" -> cyUrl)
            case None => false
          }
        }
      )
    }

    def addingTraditionalStyle(arguments: Map[String,Any], style: TraditionalStyleComponent) = {
      arguments ++ Map(
        "hideAccountMenu"      -> true,
        "showOrganisationLogo" -> true,
        "showPropositionLinks" -> style.signoutUrl.isDefined,
        "signOutUrl"           -> style.signoutUrl,
        "langSelector"         -> false
      )
    }

    def addingCssLinks(arguments: Map[String,Any], elements: CssLinksComponent) = {
      arguments ++ Map(
        "linkElems" -> elements.elements.map( e => Map("url" -> e.url, "ieVersionCondition" -> e.ieVersionCondition, "print" -> e.isPrint) )
      )
    }

    def addingScripts(arguments: Map[String,Any], scripts: ScriptsComponent) = {
      arguments ++ Map(
        "scriptElems" -> scripts.urls.map( url => Map("url" -> url) )
      )
    }

    def addingPageTitle(arguments: Map[String,Any], pageTitle: PageTitleComponent) = {
      arguments ++ Map(
        "pageTitle" -> pageTitle.title
      )
    }

    def addingInlineScript(arguments: Map[String,Any], inlineScript: InlineScriptComponent) = {
      arguments ++ Map(
        "inlineScript" -> inlineScript.script
      )
    }

    def addingSsoUrl(arguments: Map[String,Any], ssoUrl: SsoUrlComponent) = {
      arguments ++ Map(
        "ssoUrl" -> ssoUrl.url
      )
    }

    def addingGoogleAnalytics(arguments: Map[String,Any], googleAnalytics: GoogleAnalyticsComponent) = {
      arguments ++ Map(
        "googleAnalytics" -> (Map(
          "trackingId" -> googleAnalytics.trackingId,
          "cookieDomain" -> googleAnalytics.cookieDomain
        ) ++ googleAnalytics.arguments.toMap)
      )
    }

    def addingFullWidthBanner(arguments: Map[String,Any], fullWidthBanner: FullWidthBannerComponent) = {
      arguments ++ Map(
        "fullWidthBannerTitle" -> fullWidthBanner.title,
        "fullWidthBannerText" -> fullWidthBanner.text,
        "fullWidthBannerLink" -> fullWidthBanner.linkUrl,
        "fullWidthBannerDismissText" -> fullWidthBanner.dismissText,
        "fullWidthBannerGaAction" -> fullWidthBanner.gaAction
      )
    }

    def addingGetHelpForm(arguments: Map[String,Any], getHelpFormComponent: GetHelpFormComponent) = {
      arguments ++ Map(
        "getHelpForm" -> getHelpFormComponent.html
      )
    }

    def addingMainContentHeader(arguments: Map[String,Any], mainContentHeaderComponent: MainContentHeaderComponent) = {
      arguments ++ Map(
        "mainContentHeader" -> mainContentHeaderComponent.html
      )
    }

    def addingBackLinkUrl(arguments: Map[String,Any], backLinkUrlComponent: BackLinkUrlComponent) = {
      arguments ++ Map(
        "backlinkUrl" -> backLinkUrlComponent.url,
        "backlinkUrlText" -> backLinkUrlComponent.text
      )
    }

    def addingUserProperties(arguments: Map[String,Any], userPropertiesComponent: UserPropertiesComponent) = {
      arguments ++ Map(
        "isGovernmentGateway" -> userPropertiesComponent.isGovernmentGateway,
        "isSa" -> userPropertiesComponent.isSa,
        "isVerify" -> userPropertiesComponent.isVerify
      )
    }

    def addingActingAttorneyBanner(arguments: Map[String,Any], actingAttorneyBannerComponent: ActingAttorneyBannerComponent) = {
      arguments ++ Map(
        "actingAttorneyBanner" -> actingAttorneyBannerComponent.html
      )
    }

    def addingNavTitle(arguments: Map[String,Any], pageTitle: NavTitleComponent) = {
      arguments ++ Map(
        "navTitle" -> pageTitle.title
      )
    }

    def addingBetaBanner(arguments: Map[String,Any], betaBannerComponent: BetaBannerComponent) = {
      arguments ++ Map(
        "betaBanner" -> Map(
          "feedbackIdentifier" -> betaBannerComponent.feedbackIdentifier
        )
      )
    }

    val arguments = Map[String,Any]()

    components.flatten.foldLeft(arguments) {
      (acc, component) =>
        acc ++ (component match {
          case c: AccountMenuStyleComponent      => addingAccountMenuStyle(arguments, c)
          case c: TraditionalStyleComponent      => addingTraditionalStyle(arguments, c)
          case c: CssLinksComponent              => addingCssLinks(arguments, c)
          case c: ScriptsComponent               => addingScripts(arguments, c)
          case c: PageTitleComponent             => addingPageTitle(arguments, c)
          case c: InlineScriptComponent          => addingInlineScript(arguments, c)
          case c: SsoUrlComponent                => addingSsoUrl(arguments, c)
          case c: GoogleAnalyticsComponent       => addingGoogleAnalytics(arguments, c)
          case c: FullWidthBannerComponent       => addingFullWidthBanner(arguments, c)
          case c: GetHelpFormComponent           => addingGetHelpForm(arguments, c)
          case c: MainContentHeaderComponent     => addingMainContentHeader(arguments, c)
          case c: BackLinkUrlComponent           => addingBackLinkUrl(arguments, c)
          case c: UserPropertiesComponent        => addingUserProperties(arguments, c)
          case c: ActingAttorneyBannerComponent  => addingActingAttorneyBanner(arguments, c)
          case c: NavTitleComponent              => addingNavTitle(arguments, c)
          case c: BetaBannerComponent            => addingBetaBanner(arguments, c)
        })
    }

  }
}
