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

import com.reactific.sbt.ProjectPlugin.autoImport._
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.web.Import._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scoverage.coveralls.Imports.CoverallsKeys._
import play.sbt.routes.RoutesKeys._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

object Settings extends AssetsSettings {

  val applicationName = "scrupal"
  lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

  val classesIgnoredByScoverage : String = Seq[String](
    "<empty>", // Avoids warnings from scoverage
    "scrupal.core.ScrupalBuildInfo", // Generated by sbt-buildinfo
    "scrupal.test.*", // Code used only for testing
    "scrupal.core.akkahttp.*", // Not currently in use, may be deleted in future
    "router.Routes.*", // Stuff generated by the Play Routes compiler
    "router.scrupal.core.javascript.*" // Javascript routes generated by Play Routes compiler

  ).mkString(";")


  val sharedSettings = Seq(
    scalaVersion := Ver.scala,
    scalacOptions ++= Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    resolvers ++= Seq(Resolver.jcenterRepo)
  )

  lazy val clientSettings = sharedSettings ++ Seq(
    libraryDependencies ++= Dependencies.clientDependencies.value,
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Dependencies.jsDependencies,
    skip in packageJSDependencies := false,
    persistLauncher := true,
    persistLauncher in Test := false,
    coverageExcludedPackages := ".*",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  lazy val serverSettings = sharedSettings ++ Seq(
    organization := "org.scrupal",
    copyrightHolder := "Reactific Software LLC",
    copyrightYears := Seq(2013, 2014, 2015),
    developerUrl := url("http://reactific.com/"),
    titleForDocs := "Scrupal Core",
    codePackage := "scrupal.core",
    libraryDependencies ++= Dependencies.serverDependencies,
    commands += ReleaseCmd,
    LessKeys.compress in Assets := true,
    includeFilter in(Assets, LessKeys.less) := "*.less",
    excludeFilter in(Assets, LessKeys.less) := "_*.less",
    routesGenerator := InjectedRoutesGenerator,
    resolvers ++= Dependencies.all_resolvers,
    ivyLoggingLevel := UpdateLogging.Quiet,
    namespaceReverseRouter := true,
    coverageFailOnMinimum := true,
    coverageExcludedPackages := classesIgnoredByScoverage,
    coverageMinimum := 75,
    coverallsToken := Some("uoZrsbhbC0E2289tvwp3ISntZLH2yjwqX"),
    buildInfoObject := "ScrupalBuildInfo",
    buildInfoPackage := "scrupal.core",
    buildInfoKeys ++= Seq (
      "play_version" -> Ver.play,
      "akka_version" -> Ver.akka,
      "silhouette_version" -> Ver.silhouette,
      "bootstrap_version" -> Ver.bootstrap,
      "bootswatch_version" -> Ver.bootswatch,
      "font_awesome_version" -> Ver.font_awesome,
      "marked_version" -> Ver.marked,
      "jquery_version" → Ver.jquery,
      "modernizr_version" → Ver.modernizr
    ),
    unmanagedJars in sbt.Test <<= baseDirectory map { base => (base / "libs" ** "*.jar").classpath },
    maxErrors := 50
  )

  // Command for building a release
  lazy val ReleaseCmd = Command.command("elide4Release") {
    state => "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
      "client/clean" ::
      "client/test" ::
      "server/clean" ::
      "server/test" ::
      "server/dist" ::
      "set elideOptions in client := Seq()" ::
      state
  }
}



object Ver {

  val scala = "2.11.7"
  val play = "2.4.4"
  val akka = "2.3.13"
  val akka_http = "2.0.1"
  val kamon = "0.4.0"
  val silhouette = "3.0.4"
  val bootstrap = "3.3.6" // Note: must match play_bootstrap version
  val bootswatch = "3.3.5+4"
  val font_awesome = "4.3.0-3"
  val marked = "0.3.2"
  val jquery = "2.1.4"
  val modernizr = "2.8.3"
  val slickery = "0.3.8"
  val scalatags = "0.5.4"

  object shared {
    val scalaRx = "0.2.8"
    val autowire = "0.2.5"
    val booPickle = "1.1.0"
    val uTest = "0.3.1"
  }

  object client {
    val scalaDom = "0.8.2"
    val scalajsReact = "0.10.4"
    val scalaCSS = "0.3.1"
  }

  object js {
    val jQuery = "2.1.4"
    val react = "0.14.2"
  }

  object server {
    val silhouette = "3.0.4"
    val macwire = "2.1.0"
    val playScripts = "0.3.0"
  }

}
