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
package scrupal.html

import play.api.data.Form
import scrupal.core.{SiteData, HtmlContent, Context}

import scala.concurrent.Future
import scalatags.Text.all._

object Administration {

  val routes = router.scrupal.admin.routes.AdminController

  def navbar(sites : Map[Long, String], modules: Map[Symbol,String]) = {
    div(cls:="container",
      div(cls:="navbar-header",
        button(`type`:="button",cls:="navbar-toggle", data("toggle"):="collapse",data("target"):=".navbar-collapse",
          span(cls:="icon-bar"),
          span(cls:="icon-bar"),
          span(cls:="icon-bar")
        ),
        a(cls:="navbar-brand",href:="/admin","Admin")
      ),
      div(cls:="collapse navbar-collapse",
        ul(cls:="nav navbar-nav",
          li(cls:="dropdown",
            a(href:="#",cls:="dropdown-toggle",data("toggle"):="dropdown",role:="button",
              aria.haspopup:="true",aria.expanded:="false","Sites",span(cls:="caret")),
            ul(cls:="dropdown-menu",
              li(cls:="dropdown-header",a(href:=routes.doPOST("site","").url, "Create New Site")),
              li(role:="separator",cls:="divider"),
              for( (oid,name) ← sites) {
                li(a(href:=routes.doGET("site",oid.toString).url,name))
              }
            )
          ),
          li(cls:="dropdown",
            a(href:="#",cls:="dropdown-toggle",data("toggle"):="dropdown",role:="button",aria.haspopup:="true",
              aria.expanded:="false", "Modules", span(cls:="caret")),
            ul(cls:="dropdown-menu",
              for( (id,name) ← modules) {
                li(a(href:=routes.doGET("module",id.name).url, name))
              }
            )
          )
        )
      ) //  <!--/.nav-collapse -->
    )
  }

  def header : HtmlElement = {
    div(cls:="container",
      div(cls:="text-center", h1("Administration"))
    )
  }

  def left : HtmlElement = {
    div(cls:="container", h1("Left") )
  }

  def right(context : Context) : HtmlElement = {
    div(cls:="container", h3("Statistics"), scrupal_stats(context) )
  }

  def footer : HtmlElement = {
    div(cls:="text-center", reactific_copyright())
  }

  def page(context : Context, contents : HtmlContents, siteMap: Map[Long,String] ) : Future[String] = {
    val args : Arrangement = Map[String,HtmlContentsGenerator] (
      "contents" → HtmlContent(contents),
      "endscripts" → HtmlContent(emptyContents)
    )
    context.scrupal.reactPolymerLayout.page(context, args)
  }

  def introduction = {
    div(cls:="container",
      h2("Administration Help"),
      p("Welcome to Scrupal's adminstration pages. The menu at the top allows you to reach the main sections of the",
        "administrative interface for Scrupal sites. You can administer:"),
      ul(
        li(em("Sites")," - Define the sites Scrupal responds to and configure various aspects of those sites."),
        li(em("Modules"), " - Configure which modules are active."),
        li(em("Users"), " - User administration and permissions management.")
      )
    )
  }

  case class CreateSite(name: String, description: String, domainName: String, requireHttps: Boolean)

  def site_form(form: Form[CreateSite]) = {
    Seq(
      h2("Create Site (TBD)")
      /*
      helper.form(action = routes.createSite, 'id -> "myForm") {
      @helper.inputText(form("name"), 'label→"Name of site")
      @helper.inputText(form("description"), 'label→"Description of site")
      @helper.inputText(form("domainName"), 'label→"Regular expression of domain names to match for this site")
      @helper.checkbox(form("requireHttps"), 'label → "Requires HTTPS?")
      <input type="submit" value="Submit">
        }
        */
    )
  }

  def site(site : SiteData)(feedback: HtmlContents = emptyContents) : HtmlContents = {
    Seq(
      div(cls:="container",
        ul(cls:="nav nav-tabs",role:="tablist",
          li(role:="presentation", cls:="active",
            a(href:="#about",aria.controls:="about",role:="tab",data("toggle"):="tab", "About")
          ),
          li(role:="presentation",
            a(href:="#config",aria.controls:="config",role:="tab",data("toggle"):="tab", "Configuration")
          ),
          li(role:="presentation",
            a(href:="#modules",aria.controls:="modules", role:="tab", data("toggle"):="tab", "Modules")
          ),
          li(role:="presentation",
            a(href:="#modules",aria.controls:="usage", role:="tab", data("toggle"):="tab", "Usage")
          )
        ),
        div(cls:="tab-content",
          div(role:="tabpanel",cls:="tab-pane active", id:="about",
            h2(span(id:="name",contenteditable:="",site.name)),
            dl(cls:="dl-horizontal",
              dt("Description:"),     dd(span(id:="description",contenteditable:="",site.description)),
              dt("Domain Pattern:"),  dd(span(id:="domain",contenteditable:="",site.domainName)),
              dt("Requires HTTPS:"),  dd(span(id:="requireHttps", contenteditable:="",
                {if (site.requireHttps) "yes" else "no"})),
              dt("Created At"),       dd(site.createdAt()),
              dt("Modified At:"),     dd(site.modifiedAt()),
              dt("Object Id:"),       dd(site.oid.getOrElse(-1L).toString)
            ),
            button(id:="savebutton",`type`:="button",cls:="btn btn-default",aria.label:="Left Align", title:="Save",
              span(cls:="glyphicon glyphicon-upload",aria.hidden:="true")
            ),
            div(id:="message")
          ),
          div(role:="tabpanel",cls:="tab-pane",id:="config",h2("Configuration")),
          div(role:="tabpanel",cls:="tab-pane",id:="modules",h2("Modules")),
          div(role:="tabpanel",cls:="tab-pane",id:="usage",h2("Usage"))
        )
      ),
      script(`type`:="text/javascript",
        """
          |$(document).ready(function()
          |{$('# savebutton ').click(
          |  function() {
          |    var name = $('# name ').html();
          |    var description = $('# description ').html();
          |    var domain = $('# domain ').html();
          |    var https = 0;
          |    if ($('# requireHttps ').html().toLowerCase() in[ 'yes ',' true ',' on '] )
          |    {https = 1;}
          |    $.ajax(
          |      {
          |        type: 'post ',
          |        url: '@ {AdminController.updateSite(site.oid.getOrElse(0)).url} ',
          |       data: 'name = ' +name + '& description = ' +description + '& domainName = '+ domain
          |        +'& requireHttps = '+ https,
          |        success: function (data, status, xhr) {
          |        var node = $('< div class = "bg-succes" > Save completed.</ div > ');
          |        if (status != 200) {
          |          node = $('< div class = "bg-warning" > Save failed.</ div > ');
          |        }
          |        var span = $('# message ');
          |       while (span.firstChild) {
          |          span.removeChild(span.firstChild);
          |        }
          |        span.appendChild(node);
          |      }
          |      }
          |    );
          |  }
          |);}
          |);
          |""".stripMargin)
    )
  }

  def module = {
    div(cls:="container",
      ul(cls:="nav nav-tabs",role:="tablist",
        li(role:="presentation",cls:="active",
          a(href:="#about",aria.controls:="about",role:="tab",data("toggle"):="tab", "About")
        ),
        li(role:="presentation",
          a(href:="#config",aria.controls:="config",role:="tab",data("toggle"):="tab", "Configuration")
        )
      ),
      div(cls:="tab-content",
        div(role:="tabpanel", cls:="tab-pane active",id:="about", h2("About")),
        div(role:="tabpanel", cls:="tab-pane", id:="config", h2("Configuration"))
      )
    )
  }

  def error(msg: String) : HtmlElement  = {
    div(cls:="bg-warning",
      h3("Oops!"),
      p(msg)
    )
  }
}


