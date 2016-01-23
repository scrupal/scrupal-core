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


import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


/** Build Dependencies
  * This trait can be mixed in to get all of Scrupal's repository resolvers and dependent libraries.
  */
object Dependencies
{
  // val scrupal_org_releases    = "Scrupal.org Releases" at "http://scrupal.github.org/mvn/releases"
  val sonatype_snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  val google_sedis       = "Google Sedis" at "http://pk11-scratch.googlecode.com/svn/trunk/"
  val atlassian          = "Atlassian Releases" at "https://maven.atlassian.com/public/"
  val edulify            =
    Resolver.url("Edulify Repository", url("https://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
//val scala_lang         = "Scala Language" at "http://mvnrepository.com/artifact/org.scala-lang/"
//val geolocation        = "geolocation repository" at "http://blabluble.github.com/modules/releases/"

  val all_resolvers : Seq[Resolver] = Seq (
    sonatype_snapshots, google_sedis, atlassian, edulify, Resolver.jcenterRepo
  )

  // Things we borrow from Play Framework
  val play_cache              = "com.typesafe.play"         %% "play-cache"               % Ver.play
  val play_docs               = "com.typesafe.play"         %% "play-docs"                % Ver.play
  val play_filters            = "com.typesafe.play"         %% "filters-helpers"          % Ver.play
  val play_iteratees          = "com.typesafe.play"         %% "play-iteratees"           % Ver.play
  val play_jdbc               = "com.typesafe.play"         %% "play-jdbc"                % Ver.play
  val play_json               = "com.typesafe.play"         %% "play-json"                % Ver.play
  val play_ws                 = "com.typesafe.play"         %% "play-ws"                  % Ver.play

  // Play Plugins
  val play_bootstrap          = "com.adrianhurt"            %% "play-bootstrap"           % "1.0-P24-B3-SNAPSHOT"
  val play_geolocation        = "com.edulify"               %% "geolocation"              % "1.4.1"
  val play_html_compressor    = "com.mohiva"                %% "play-html-compressor"     % "0.5.0"
  val play_plugins_mailer     = "com.typesafe.play"         %% "play-mailer"              % "3.0.0-M1"
  val play_plugins_redis      = "com.typesafe.play.modules" %% "play-modules-redis"       % "2.4.1"
  val play_silhouette         = "com.mohiva"                %% "play-silhouette"          % Ver.silhouette
  val play_slick              = "com.typesafe.play"         %% "play-slick"               % "1.1.1"
  val play_slick_evols        = "com.typesafe.play"         %% "play-slick-evolutions"    % "1.1.1"

  // Akka Stuff
  val akka_actor              = "com.typesafe.akka"         %% "akka-actor"               % Ver.akka
  val akka_slf4j              = "com.typesafe.akka"         %% "akka-slf4j"               % Ver.akka
  val akka_http               = "com.typesafe.akka"         %% "akka-http-experimental"   % Ver.akka_http

  // Fundamental Libraries
  val shapeless               = "com.chuusai"               %% "shapeless"                % "2.2.1"
  val scala_arm               = "com.jsuereth"              %% "scala-arm"                % "1.4"
  val scalatags               = "com.lihaoyi"               %% "scalatags"                % Ver.scalatags

  // Database, Caches, Serialization, Data Storage stuff
  val slickery                = "com.reactific"             %% "slickery"                 % Ver.slickery

  // WebJars We Use
  val webjars_play            = "org.webjars"               %% "webjars-play"             % Ver.webjar.play
  val wj_polymer              = "org.webjars"               % "polymer"                   % Ver.webjar.polymer
  val wj_webcomponentsjs      = "org.webjars"               % "webcomponentsjs"           % Ver.webjar.webcomponentsjs
  val wj_bootswatch           = "org.webjars"               % "bootswatch"                % Ver.webjar.bootswatch
  val wj_marked               = "org.webjars"               % "marked"                    % (Ver.webjar.marked+"-1")
  val wj_font_awesome         = "org.webjars"               % "font-awesome"              % Ver.webjar.font_awesome
  val wj_modernizr            = "org.webjars"               % "modernizr"                 % Ver.webjar.modernizr
  val wj_jquery               = "org.webjars"               % "jquery"                    % Ver.webjar.jquery

  // Hashing Algorithms
  val pbkdf2                  = "io.github.nremond"         %% "pbkdf2-scala"             % "0.4"
  val bcrypt                  = "org.mindrot"               % "jbcrypt"                   % "0.3m"
  val scrypt                  = "com.lambdaworks"           % "scrypt"                    % "1.4.0"

  val macwire_macros = "com.softwaremill.macwire" %% "macros" % Ver.server.macwire % "provided"
  val macwire_util = "com.softwaremill.macwire" %% "util" % Ver.server.macwire
  val macwire_proxy = "com.softwaremill.macwire" %% "proxy" % Ver.server.macwire

  // Kamon Monitoring
  // TODO: Utilize Kamon Monitoring
  val kamon_core              = "io.kamon"                  %% "kamon-core"                % Ver.kamon
  val kamon_scala             = "io.kamon"                  %% "kamon-scala"               % Ver.kamon
  val kamon_akka              = "io.kamon"                  %% "kamon-akka"                % Ver.kamon
  val kamon_log_reporter      = "io.kamon"                  %% "kamon-log-reporter"        % Ver.kamon
  val kamon_play              = "io.kamon"                  %% "kamon-play"                % Ver.kamon
  val kamon_system_metrics    = "io.kamon"                  %% "kamon-system_metrics"      % Ver.kamon
  val kamon_annotation        = "io.kamon"                  %% "kamon-annotation"          % Ver.kamon

  // Miscellaneous
  val config                  = "com.typesafe"              %  "config"                   % "1.3.0"
  val commons_lang3           = "org.apache.commons"        % "commons-lang3"             % "3.4"
  val hotspot_profiler        = "com.reactific"             %% "hotspot-profiler"         % "0.3.0"

  object Test {
    val akka             = "com.typesafe.akka"        %% "akka-testkit"             % Ver.akka        % "test"
    val commons_io       = "commons-io"                % "commons-io"               % "2.4"           % "test"
    val silhouette       = "com.mohiva"               %% "play-silhouette-testkit"  % Ver.silhouette  % "test"
    val slickery         = "com.reactific"            %% "slickery-testkit"         % Ver.slickery    % "test"

    // WARNING: The nu.validator depends on Rhino 1.7 which is built with JDK 1.6 and doesn't work under 1.7 or 1.8.
    // WARNING: Consequently we have included our own lib/vnu.jar that was built with 1.8 (for all its dependencies).
    // WARNING: At some point we'd like to go back to using the maven loaded version, but only after this issue is
    // WARNING: resolved. See Issue #206 (https://github.com/validator/validator/issues/206)
    // val nu_validator     = ("nu.validator" % "validator" % "16.1.1" % "test").exclude("org.eclipse.jetty", "*")
    // val rhino            = "org.mozilla" % "rhino" % "1.7.7" % "test"
  }

  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % Ver.shared.autowire,
    "me.chrons"   %%% "boopickle" % Ver.shared.booPickle,
    "com.lihaoyi" %%% "scalarx" % Ver.shared.scalaRx,
    "com.lihaoyi" %%% "utest" % Ver.shared.uTest
  ))

  val serverDependencies = Seq(
    macwire_macros, macwire_util, macwire_proxy,
    pbkdf2, bcrypt, scrypt,
    commons_lang3, config, shapeless, scala_arm, slickery, scalatags,
    akka_actor, akka_http, akka_slf4j,
    play_json, play_iteratees, play_plugins_mailer, play_plugins_redis, play_slick, play_slick_evols,
    play_silhouette, play_bootstrap, play_html_compressor, // play_geolocation,
    webjars_play, wj_polymer, wj_webcomponentsjs, wj_bootswatch, wj_marked, wj_font_awesome, wj_modernizr, wj_jquery,
    // kamon_core, kamon_scala, kamon_akka, kamon_log_reporter, kamon_play, kamon_system_metrics, kamon_annotation,
    Test.akka, Test.commons_io, Test.silhouette, Test.slickery //, Test.nu_validator, Test.rhino

    // "com.vmunier" %% "play-scalajs-scripts" % Ver.server.playScripts
  )

  val clientDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % Ver.client.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % Ver.client.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % Ver.client.scalaCSS,
    "org.scala-js" %%% "scalajs-dom" % Ver.client.scalaDom
  ))

  val jsDependencies = Seq(
    "org.webjars.npm" % "react" % Ver.js.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.npm" % "react-dom" % Ver.js.react / "react-dom.js" commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js",
    "org.webjars" % "jquery" % Ver.js.jQuery / "jquery.js" minified "jquery.min.js",
    RuntimeDOM % "test"
  )
}
