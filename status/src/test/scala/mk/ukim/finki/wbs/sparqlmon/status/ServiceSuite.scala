package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL

import cats.effect.IO
import io.circe.generic.auto._
import munit.FunSuite
import org.http4s.{ HttpVersion, Method, Request }
import org.http4s.circe._
import org.http4s.implicits._

import mk.ukim.finki.wbs.sparqlmon.model._

class ServiceSuite extends FunSuite {

  implicit private val sr = new TestStatusRepository[IO]

  implicit private val statusDecoder            = jsonOf[IO, Status]
  implicit private val malformedUrlErrorDecoder = jsonOf[IO, Error.MalformedUrlError]
  implicit private val vectorDecoder            = jsonOf[IO, Vector[Status]]

  test("status") {
    val url      = new URL("http://dbpedia.org/sparql")
    val expected = Status(url, None)
    val req      = Request[IO](Method.GET, uri"/status".withQueryParam("url", url.toString))
    val test     = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, org.http4s.Status.Ok))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Status]
    } yield assertEquals(body, expected)
    test.unsafeRunSync()
  }

  test("malformed url") {
    val req  = Request[IO](Method.GET, uri"/status".withQueryParam("url", "blah"))
    val test = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, org.http4s.Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Error.MalformedUrlError]
    } yield assertEquals(body, Error.MalformedUrlError("blah"))
    test.unsafeRunSync()
  }

  test("overview") {
    val req  = Request[IO](Method.GET, uri"/status/overview")
    val test = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, org.http4s.Status.Ok))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Vector[Status]]
    } yield assertEquals(body, Vector.empty)
    test.unsafeRunSync()
  }
}
