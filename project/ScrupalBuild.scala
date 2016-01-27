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

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

import play.sbt.{PlayLayoutPlugin, PlayScala}

import playscalajs.PlayScalaJS.autoImport._
import playscalajs.ScalaJSPlay

import sbt._
import sbt.Keys._
import sbt.Project.projectToRef

import sbtbuildinfo.BuildInfoPlugin

import scrupal.sbt.ScrupalPlugin

import scoverage.ScoverageSbtPlugin

object ScrupalBuild extends Build {

  lazy val shared = (crossProject.crossType(CrossType.Pure) in file("scrupal-shared"))
    .settings(Settings.sharedSettings:_*)
    .settings(
      name := "scrupal-shared",
      libraryDependencies ++= Dependencies.sharedDependencies.value
    )
    // .jsConfigure()
    .jvmSettings()
    .jsSettings()

  lazy val sharedJVM = shared.jvm
  lazy val sharedJS = shared.js

  lazy val client = Project("scrupal-client", file("scrupal-client")).
    enablePlugins(ScalaJSPlugin, ScalaJSPlay).
    // WARNING: This yields a "These plugins were both included and excluded error"
    // .disablePlugins(ScoverageSbtPlugin)
    settings(Settings.clientSettings).
    dependsOn(sharedJS)

  lazy val reactPolymer = Project("scrupal-react-polymer", file("react-polymer")).
    enablePlugins(ScalaJSPlugin, ScalaJSPlay).
    settings(Settings.reactPolymerSettings).
    dependsOn(client,sharedJS)

  lazy val jsProjects = Seq(client, reactPolymer)

  lazy val server = Project("scrupal-server", file("scrupal-server")).
    disablePlugins(PlayLayoutPlugin).
    enablePlugins(PlayScala, BuildInfoPlugin, ScrupalPlugin, ScoverageSbtPlugin).
    settings(Settings.sbt_web_settings).
    settings(Settings.pipeline_settings).
    settings(Settings.less_settings).
    settings(Settings.serverSettings).
    settings(Seq(
      scalaJSProjects := jsProjects
    )).
    dependsOn(sharedJVM).
    aggregate(jsProjects.map(projectToRef): _*)

  lazy val root : Project = Project("scrupal-core", file(".")).
    disablePlugins(PlayLayoutPlugin).
    enablePlugins(ScrupalPlugin).
    settings(Settings.coreSettings).
    settings(
      publish := {},
      publishLocal := {},
      onLoad in Global := (Command.process("project scrupal-server", _: State)) compose (onLoad in Global).value
    ).
    aggregate(
      sharedJVM,
      sharedJS,
      client,
      reactPolymer,
      server
    )


  override def rootProject = Some(root)
}
