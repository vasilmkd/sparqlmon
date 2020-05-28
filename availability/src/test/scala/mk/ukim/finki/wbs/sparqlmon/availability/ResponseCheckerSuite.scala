package mk.ukim.finki.wbs.sparqlmon.availability

import cats.effect.IO
import io.circe.literal._
import munit.FunSuite
import org.http4s.{ Response, Status }
import org.http4s.circe._

class ResponseCheckerSuite extends FunSuite {

  test("ask response body cannot be serialized to json") {
    val res = Response[IO](Status.Ok).withEntity("<html></html>")
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("ask response status not ok") {
    val res = Response[IO](Status.BadRequest).withEmptyBody
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("ask response bad") {
    val res = Response[IO](Status.Ok).withEntity(json"{}")
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("ask response noboolean") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"noboolean": true}""")
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("ask response true") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"boolean": true}""")
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isDefined)
  }

  test("ask response false") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"boolean": false}""")
    assert(ResponseChecker.checkAskResponse(res).value.unsafeRunSync().isDefined)
  }

  test("select response body cannot be serialized to json") {
    val res = Response[IO](Status.Ok).withEntity("<html></html>")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response status not ok") {
    val res = Response[IO](Status.BadRequest).withEmptyBody
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response bad") {
    val res = Response[IO](Status.Ok).withEntity(json"{}")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response no results") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"noresults": {}}""")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response no bindings") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"nobindings": [{"s": {}}]}}""")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response empty bindings") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"bindings": []}}""")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isEmpty)
  }

  test("select response sparql") {
    val res = Response[IO](Status.Ok).withEntity(json"""{"results": {"bindings": [{"s": {}}]}}""")
    assert(ResponseChecker.checkSelectResponse(res).value.unsafeRunSync().isDefined)
  }
}
