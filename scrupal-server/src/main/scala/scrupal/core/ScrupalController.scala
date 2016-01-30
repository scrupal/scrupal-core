package scrupal.core

import com.reactific.helpers.Patterns
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._

import scrupal.utils.ScrupalComponent


/** A Generic Play Controller For Use With Scrupal.
  * This just bundles together some things that all the Scrupal controllers use.
  */
case class ScrupalController(scrupal : Scrupal, messagesApi : MessagesApi)
  extends Controller with I18nSupport with ScrupalComponent {

  def contextFor(thing: String, request: RequestHeader) : Option[Context] ={
    Some(scrupal.contextForRequest(request))
  }

  type RxFinder = (Context, String, RequestHeader) ⇒ Option[Reactor]

  def appReactorFor(context: Context, thing: String, request: RequestHeader) : Option[Reactor] = {
    context.site match {
      case Some(site) ⇒
        site.provideFor[ApplicationProvider].lift(request)
      case None ⇒
        None
    }
  }

  def apiReactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = {
    context.site match {
      case Some(site) ⇒
        site.provideFor[EntityProvider].lift(request)
      case None ⇒
        None
    }
  }

  def thingReactorFor(context : Context, thing : String, request: RequestHeader) : Option[Reactor] = {
    context.site match {
      case Some(site) ⇒
        site.reactorFor(request)
      case None ⇒
        None
    }
  }

  protected def cleanse(str : String) : String = {
    str.toLowerCase.replaceAll(Patterns.NotAllowedInUrl.pattern.pattern, "-")
  }

  def apiGET(entity: String, what : String) = apiReactor(cleanse(entity), what)
  def apiPUT(entity: String, what : String) = apiReactor(cleanse(entity), what)
  def apiPOST(entity: String, what : String) = apiReactor(cleanse(entity), what)
  def apiOPTIONS(entity: String, what : String) = apiReactor(cleanse(entity), what)
  def apiDELETE(entity: String, what : String) = apiReactor(cleanse(entity), what)
  def apiHEAD(entity: String, what : String) = apiReactor(cleanse(entity), what)

  def apiReactor(api : String, what : String) = reactorFor(4, api, what, apiReactorFor)

  def appGET(app: String, what : String) = appReactor(cleanse(app), what)
  def appPUT(app: String, what : String) = appReactor(cleanse(app), what)
  def appPOST(app: String, what : String) = appReactor(cleanse(app), what)
  def appOPTIONS(app: String, what : String) = appReactor(cleanse(app), what)
  def appDELETE(app: String, what : String) = appReactor(cleanse(app), what)
  def appHEAD(app: String, what : String) = appReactor(cleanse(app), what)

  def appReactor(app : String, what : String) = reactorFor(4, app, what, appReactorFor)

  def thingGET(thing: String, what : String) = thingReactor(cleanse(thing), what)
  def thingPUT(thing: String, what : String) = thingReactor(cleanse(thing), what)
  def thingPOST(thing: String, what : String) = thingReactor(cleanse(thing), what)
  def thingOPTIONS(thing: String, what : String) = thingReactor(cleanse(thing), what)
  def thingDELETE(thing: String, what : String) = thingReactor(cleanse(thing), what)
  def thingHEAD(thing: String, what : String) = thingReactor(cleanse(thing), what)

  def thingReactor(thing : String, what : String) = reactorFor(0, thing, what, thingReactorFor)

  def reactorFor(prefixLenToDrop : Int, thing: String, what : String, rxFinder : RxFinder ) = {
    Action.async { implicit request : Request[AnyContent] ⇒
      val rh: RequestHeader = request.copy(path = request.path.drop(prefixLenToDrop))
      log.debug(s"RequestHeader(method=${rh.method}, host=${rh.host}, uri=${rh.uri}, path=${rh.path}")
      contextFor(thing, rh) match {
        case Some(context) ⇒
          rxFinder(context, thing, rh) match {
            case Some(reactor) ⇒
              reactor.resultFrom(context, request)
            case None ⇒
              Reactor.unlocatable(s"No reactor found for $rh").resultFrom(context, request)
          }
        case None ⇒
          Reactor.unlocatable(s"No context found for $rh").resultFrom(Context(scrupal), request)
      }
    }
  }
}
