resolvers ++=  Seq( Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots") )

addSbtPlugin("org.scrupal" % "scrupal-sbt" % "0.4.0" )

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.6")

addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.2.8")
