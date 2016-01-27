package scrupal.html

import scalatags.Text.all._

/** Layout for Polymer based pages */
trait PolymerLayout extends DetailedPageLayout {

  override def otherMeta(args : Arguments) = {
    Map(
      "theme-color" → "#2E3AA1", // Chrome for Android theme color
      "msapplication-TileColor" → "#3372DF", // Win8 tile color
      "application-name" → application(args).getOrElse("Polymer"),
      "mobile-web-app-capable" → "yes",
      "apple-mobile-web-app-capable" -> "yes",
      "apple-mobile-web-app-status-bar-style" -> "black",
      "apple-mobile-web-app-title" → application(args).getOrElse("Polymer"),
      "msapplication-TileImage" → "images/touch/ms-touch-icon-144x144-precomposed.png"
    )
  }

  override def otherLinks(args : Arguments) = {
    Seq(
      LinkTag("apple-touch-icon", "images/touch/apple-touch-icon.png", "image/png"),
      LinkTag("manifest", "manifest.json")
    )
  }

  override def imports(args : Arguments) = {
    super.imports(args) ++ Seq(assets.webjar("polymer","polymer.html").url)
  }

  override def javascriptLinks(args : Arguments) = {
    super.javascriptLinks(args) ++ Seq(assets.webjar("webcomponentsjs","webcomponents-lite.js").url)

  }

}
