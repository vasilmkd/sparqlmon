package mk.ukim.finki.wbs.sparqlmon.registration

import cats.effect.IO
import io.circe.literal._
import munit.FunSuite
import org.http4s.{ Response, Status }
import org.http4s.circe._

class ResponseCheckerSuite extends FunSuite {

  test("body cannot be serialized to json") {
    val res = Response[IO](Status.Ok).withEntity("<html></html>")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("status not ok") {
    val res = Response[IO](Status.BadRequest).withEmptyBody
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("bad") {
    val res = Response[IO](Status.Ok).withEntity(json"{}")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("no results") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"noresults": {}}""")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("no bindings") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"nobindings": [{"s": {}}]}}""")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("empty bindings") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"bindings": []}}""")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isEmpty)
  }

  test("sparql") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"bindings": [{"s": {}}]}}""")
    assert(ResponseChecker.checkResponse(res).unsafeRunSync().isDefined)
  }
}
