package scrupal.html

import org.scalajs.dom
import scalatags.JsDom

/** Polymer Tags Object
  * This provides an easily importable accessor for the various classifications of polymer tags and attributes.
  * This is intended for client side (JS) DOM-based construction of HTML using polymer attributes. See the
  * equivalent server side construct
  */
object Polymer {

  object iron extends JsDom.Cap
    with PolymerIronTags[dom.Element, dom.Element, dom.Node]
    with PolymerIronAttributes[dom.Element, dom.Element, dom.Node]

  object paper extends JsDom.Cap
    with PolymerPaperTags[dom.Element, dom.Element, dom.Node]
}

