package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL

import cats.effect.IO
import cats.implicits._
import io.circe.generic.auto._
import munit.FunSuite
import org.http4s.{ HttpVersion, Method, Request, Status }
import org.http4s.circe._
import org.http4s.implicits._

import mk.ukim.finki.wbs.sparqlmon.message._

class ServiceSuite extends FunSuite {

  implicit private val ar = new TestAvailabilityRepository[IO]

  implicit private val availabilityDecoder      = jsonOf[IO, Availability]
  implicit private val malformedUrlErrorDecoder = jsonOf[IO, Error.MalformedUrlError]

  test("availability") {
    val url      = new URL("http://dbpedia.org/sparql")
    val expected = Availability(url, Vector.empty)
    val req      = Request[IO](Method.GET, uri"/availability".withQueryParam("url", url.show))
    val test     = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.Ok))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Availability]
    } yield assertEquals(body, expected)
    test.unsafeRunSync()
  }

  test("malformed url") {
    val req  = Request[IO](Method.GET, uri"/availability".withQueryParam("url", "blah"))
    val test = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Error.MalformedUrlError]
    } yield assertEquals(body, Error.MalformedUrlError("blah"))
    test.unsafeRunSync()
  }
}
