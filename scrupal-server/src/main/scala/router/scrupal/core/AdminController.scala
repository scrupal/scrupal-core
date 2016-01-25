/**********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
  *                                                                                                                    *
  * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
  *                                                                                                                    *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
  * with the License. You may obtain a copy of the License at                                                          *
  *                                                                                                                    *
  *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
  *                                                                                                                    *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
  * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
  * the specific language governing permissions and limitations under the License.                                     *
  **********************************************************************************************************************/
package router.scrupal.core


import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}

import play.api.mvc._
import scrupal.core._
import scrupal.html.Administration.CreateSite
import scrupal.html._

import scala.concurrent.{ExecutionContext, Future}

import scalatags.Text.all._

class AdminController(val scrupal : Scrupal, val messagesApi : MessagesApi) extends ScrupalController {

  private def makePage(result: Status, elem: HtmlElement) : Future[Result] = {
    makePage(result, Seq(elem))
  }

  private def makePage(result: Status, contents: HtmlContents) : Future[Result] = {

    flatMapQuery((schema) ⇒ schema.sites.mapNames()) { (siteMap, ec : ExecutionContext) ⇒
      implicit val ec2 = ec
      val context = Context(scrupal)
      Administration.page(context, contents, siteMap).map { content ⇒ result(content) }
    }
  }

  def index() = Action.async { request : Request[AnyContent] ⇒
    makePage(Ok, Administration.introduction)
  }

  def postSiteJson() = Action.async { request : Request[AnyContent] ⇒
    request.body.asJson match {
      case Some(json) ⇒
        makePage(NotImplemented, p("JSON Creation Of Sites Not Yet Supported"))
      case None ⇒
        makePage(BadRequest, p("Expected JSON content"))
    }
  }


  val createSiteForm = Form[CreateSite](
    mapping(
      "name" → nonEmptyText(maxLength=64),
      "description" → nonEmptyText(maxLength=255),
      "domainName" → nonEmptyText(maxLength=255),
      "requireHttps" → boolean
    )(CreateSite.apply)(CreateSite.unapply)
  )

  def createSite() = Action.async { implicit request : Request[AnyContent] ⇒
    createSiteForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        makePage(BadRequest, Administration.site_form(formWithErrors))
      },
      data => {
        /* binding success, you get the actual value. */
        val siteData = SiteData(data.name, data.domainName,data.description,data.requireHttps)
        mapQuery( (schema) ⇒ schema.sites.create(siteData) ) { (id : Long, ec: ExecutionContext) ⇒
          val site = Site(siteData.copy(oid=Some(id)))(scrupal)
          Redirect(router.scrupal.core.routes.AdminController.site(id))
        }
      }
    )
  }

  def updateSite(id : Long) = Action.async { implicit request : Request[AnyContent] ⇒
    flatMapQuery((schema) ⇒ schema.sites.byId(id)) {
      case (Some(siteData),ec) ⇒
        val boundForm = createSiteForm.bindFromRequest
        boundForm.fold(
          formWithErrors ⇒ {
            val page = Administration.site(siteData)(div(cls:="bg-warning","Save failed."))
            makePage(BadRequest, page)
          },
          data ⇒ {
            val newSiteData : SiteData = siteData.copy(name=data.name, description=data.description, domainName=data.domainName, requireHttps=data.requireHttps)
            flatMapQuery((schema) ⇒ schema.sites.update(newSiteData)) { (r,ec) ⇒
              makePage(Ok, Administration.site(siteData)(div(cls:="bg-success","Saved.")))
            }
          }
        )
      case (None,ec) ⇒
        makePage(NotFound, Administration.error(s"Site #$id was not found."))
    }
  }

  def newSite() = Action.async { implicit request : Request[AnyContent] ⇒
    makePage(Ok, Administration.site_form(createSiteForm))
  }

  def site(id : Long) = Action.async { implicit request : Request[AnyContent] ⇒
    flatMapQuery((schema) ⇒ schema.sites.byId(id)) {
      case (Some(siteData),ec) ⇒
        makePage(Ok, Administration.site(siteData)())
      case (None,ec) ⇒
        makePage(NotFound, Administration.error(s"Site #$id was not found."))
    }
  }

  def module(id: String) = Action.async { implicit request : Request[AnyContent] ⇒
    makePage(Ok, Administration.module())
  }

}
