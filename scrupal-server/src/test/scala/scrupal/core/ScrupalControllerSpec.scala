package scrupal.core

import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.test.{ControllerSpecification, SharedTestScrupal}

/** Test Cases For ScrupalController */
class ScrupalControllerSpec extends ControllerSpecification("ScrupalController") with SharedTestScrupal {

  def testCases = Seq(
    Case("GET","/api/feet/42/nada",Unimplemented,"feet.retrieveById(id=42,details=nada)"),
    Case("GET","/api/feet/forty-two/nada",Unimplemented,"feet.retrieveByName(name=forty-two,details=nada)"),
    Case("HEAD","/api/feet/42/nada",Unimplemented,"feet.infoById(id=42,details=nada)"),
    Case("HEAD","/api/feet/forty-two/nada",Unimplemented,"feet.infoByName(name=forty-two,details=nada)"),
    Case("OPTIONS","/api/feet/nada",Unimplemented,"feet.query(details=nada)"),
    Case("POST","/api/feet/nada",Unimplemented,"feet.create(details=nada)"),
    Case("PUT","/api/feet/42/nada",Unimplemented,"feet.updateById(id=42,details=nada)"),
    Case("PUT","/api/feet/forty-two/nada",Unimplemented,"feet.updateByName(name=forty-two,details=nada)"),
    Case("DELETE","/api/feet/42/nada",Unimplemented,"feet.deleteById(id=42,details=nada)"),
    Case("DELETE","/api/feet/forty-two/nada",Unimplemented,"feet.deleteByName(name=forty-two,details=nada)"),
    Case("GET","/api/books/42/nada",Unimplemented,"books.retrieveById(id=42,details=nada)"),
    Case("GET","/api/books/forty-two/nada",Unimplemented,"books.retrieveByName(name=forty-two,details=nada)"),
    Case("HEAD","/api/books/42/nada",Unimplemented,"books.infoById(id=42,details=nada)"),
    Case("HEAD","/api/books/forty-two/nada",Unimplemented,"books.infoByName(name=forty-two,details=nada)"),
    Case("OPTIONS","/api/books/nada",Unimplemented,"books.query(details=nada)"),
    Case("POST","/api/books/nada",Unimplemented,"books.create(details=nada)"),
    Case("PUT","/api/books/42/nada",Unimplemented,"books.updateById(id=42,details=nada)"),
    Case("PUT","/api/books/forty-two/nada",Unimplemented,"books.updateByName(name=forty-two,details=nada)"),
    Case("DELETE","/api/books/42/nada",Unimplemented,"books.deleteById(id=42,details=nada)"),
    Case("DELETE","/api/books/forty-two/nada",Unimplemented,"books.deleteByName(name=forty-two,details=nada)"),
    Case("GET","/api/foot/42/facet/facet_id/nada",Unimplemented,"foot.getById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("GET","/api/foot/forty-two/facet/facet_id/nada",Unimplemented,"foot.getByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("HEAD","/api/foot/42/facet/facet_id/nada",Unimplemented,"foot.facetInfoById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("HEAD","/api/foot/forty-two/facet/facet_id/nada",Unimplemented,"foot.facetInfoByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("OPTIONS","/api/foot/42/facet/nada",Unimplemented,"foot.findById(id=42,facet=facet,details=nada)"),
    Case("OPTIONS","/api/foot/forty-two/facet/nada",Unimplemented,"foot.findByName(name=forty-two,facet=facet,details=nada)"),
    Case("POST","/api/foot/42/facet/nada",Unimplemented,"foot.addById(id=42,facet=facet,details=nada)"),
    Case("POST","/api/foot/forty-two/facet/nada",Unimplemented,"foot.addByName(name=forty-two,facet=facet,details=nada)"),
    Case("PUT","/api/foot/42/facet/facet_id/nada",Unimplemented,"foot.setById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("PUT","/api/foot/forty-two/facet/facet_id/nada",Unimplemented,"foot.setByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("DELETE","/api/foot/42/facet/facet_id/nada",Unimplemented,"foot.removeById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("DELETE","/api/foot/forty-two/facet/facet_id/nada",Unimplemented,"foot.removeByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("GET","/api/book/42/facet/facet_id/nada",Unimplemented,"book.getById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("GET","/api/book/forty-two/facet/facet_id/nada",Unimplemented,"book.getByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("HEAD","/api/book/42/facet/facet_id/nada",Unimplemented,"book.facetInfoById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("HEAD","/api/book/forty-two/facet/facet_id/nada",Unimplemented,"book.facetInfoByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("OPTIONS","/api/book/42/facet/nada",Unimplemented,"book.findById(id=42,facet=facet,details=nada)"),
    Case("OPTIONS","/api/book/forty-two/facet/nada",Unimplemented,"book.findByName(name=forty-two,facet=facet,details=nada)"),
    Case("POST","/api/book/42/facet/nada",Unimplemented,"book.addById(id=42,facet=facet,details=nada)"),
    Case("POST","/api/book/forty-two/facet/nada",Unimplemented,"book.addByName(name=forty-two,facet=facet,details=nada)"),
    Case("PUT","/api/book/42/facet/facet_id/nada",Unimplemented,"book.setById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("PUT","/api/book/forty-two/facet/facet_id/nada",Unimplemented,"book.setByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("DELETE","/api/book/42/facet/facet_id/nada",Unimplemented,"book.removeById(id=42,facet=facet,facet_id=facet_id,details=nada)"),
    Case("DELETE","/api/book/forty-two/facet/facet_id/nada",Unimplemented,"book.removeByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"),
    Case("GET", "/other/foo", Successful, "foo"),
    Case("PUT", "/other/nada", Unimplemented, "nada"),
    Case("POST", "/other/x", Unlocatable, ""),
    Case("OPTIONS", "/other/nada", Unlocatable, ""),
    Case("HEAD", "/other/foo", Unlocatable, ""),
    Case("DELETE", "/other/nada", Unlocatable, "")
  )

  "ScrupalController" should {
    "yield NotImplemented for No context" in {
      class TestController(scrupal: Scrupal, messagesApi: MessagesApi) extends ScrupalController(scrupal, messagesApi) {
        def pathPrefix: String = "foo"
        override def apiReactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        override def appReactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        override def contextFor(thing: String, request: RequestHeader): Option[Context] = None
      }

      val ctrlr = new TestController(scrupal, scrupal.messagesApi)
      val req = FakeRequest("GET", "/api/foo/bar")
      route(scrupal.application, req) match {
        case Some(fr) ⇒
          val result = await(fr)
          result.header.status must beEqualTo(Status.NOT_FOUND)
        case None ⇒
          0 must beEqualTo(Status.NOT_FOUND)
      }
    }

    "yield NotImplemented for No route" in {
      class TestController(scrupal: Scrupal, messagesApi: MessagesApi) extends ScrupalController(scrupal, messagesApi) {
        def pathPrefix: String = "foo"
        override def appReactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        override def apiReactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        override def contextFor(thing: String, request: RequestHeader): Option[Context] = Some(Context(scrupal))
      }

      val ctrlr = new TestController(scrupal, scrupal.messagesApi)
      val req = FakeRequest("GET", "/api/foo/bar")
      route(scrupal.application, req) match {
        case Some(fr) ⇒
          val result = await(fr)
          result.header.status must beEqualTo(Status.NOT_FOUND)
        case None ⇒
          0 must beEqualTo(Status.NOT_FOUND)
      }
    }

    "not provide route for nonsense" in withScrupal("nonsense_route_failure") { scrupal ⇒
      val context = Context(scrupal)
      val request = FakeRequest("GET", "/app/totally/fake/route")
      scrupal.scrupalController.appReactorFor(context, "nada", request) must beEqualTo(None)
    }

  }
}
