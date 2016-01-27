package scrupal.core

import play.api.i18n.I18nSupport
import play.api.mvc._
import scrupal.utils.ScrupalComponent

import scala.concurrent.Future

/** A Generic Play Controller For Use With Scrupal.
  * This just bundles together some things that all the Scrupal controllers use.
  */
trait ScrupalController extends Controller with I18nSupport with ScrupalComponent {

  def scrupal : Scrupal

  val pathPrefix: String

  def contextFor(thing: String, request: RequestHeader) : Option[Context]

  def reactorFor(context : Context, thing: String, request: RequestHeader) : Option[Reactor]


  protected def cleanse(str : String) : String = {
    str.toLowerCase.replaceAll("[^a-z0-9-_~#]","-")
  }

  def doGET(thing: String, what : String) = invokeReactor(cleanse(thing), what)
  def doPUT(thing: String, what : String) = invokeReactor(cleanse(thing), what)
  def doPOST(thing: String, what : String) = invokeReactor(cleanse(thing), what)
  def doOPTIONS(thing: String, what : String) = invokeReactor(cleanse(thing), what)
  def doDELETE(thing: String, what : String) = invokeReactor(cleanse(thing), what)
  def doHEAD(thing: String, what : String) = invokeReactor(cleanse(thing), what)

  def invokeReactor(thing: String, what : String) = Action.async { implicit request : Request[AnyContent] ⇒
  val prefix = s"/$pathPrefix/"
  if (request.path.startsWith(prefix)) {
      val rh: RequestHeader = request.copy(path = request.path.toLowerCase.drop(prefix.length - 1))
      log.debug(s"RequestHeader(method=${rh.method}, host=${rh.host}, uri=${rh.uri}, path=${rh.path}")
      contextFor(thing, rh) match {
        case Some(context) ⇒
          reactorFor(context, thing, rh) match {
            case Some(reactor) ⇒
              reactor.resultFrom(context, request)
            case None ⇒
              UnimplementedReactor(s"No reactor found for $rh").resultFrom(context, request)
          }
        case None ⇒
          UnimplementedReactor(s"No context found for $rh").resultFrom(Context(scrupal), request)
      }
    } else {
      Future.successful {
        NotFound(s"${request.path}")
      }
    }
  }
}
