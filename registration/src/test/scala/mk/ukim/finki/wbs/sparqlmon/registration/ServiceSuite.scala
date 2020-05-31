package mk.ukim.finki.wbs.sparqlmon.registration

import cats.effect.IO
import cats.implicits._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.literal._
import io.circe.syntax._
import munit.FunSuite
import org.http4s.{ HttpVersion, Method, Request, Status }
import org.http4s.circe._
import org.http4s.implicits._

import mk.ukim.finki.wbs.sparqlmon.message._

class ServiceSuite extends FunSuite {

  test("endpoints") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    val req                             = Request[IO](Method.GET, uri"/endpoints")
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.Ok))
      _    <- IO(assertEquals(res.headers.find(_.name.value === "Content-Type").get.value, "application/json"))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body.as[Set[Endpoint]].toOption.get, TestEndpointRepository.testEndpoints)
    test.unsafeRunSync()
  }

  test("register") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    implicit val encoder                = jsonEncoderOf[IO, Endpoint]
    val req                             = Request[IO](Method.POST, uri"/register").withEntity(TestEndpointRepository.testEndpoint)
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.Ok))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[String]
    } yield assertEquals(body, "")
    test.unsafeRunSync()
  }

  test("malformed") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    val req                             = Request[IO](Method.POST, uri"/register").withEntity(json"{}")
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body, Error.MalformedRegistrationRequest.asJson)
    test.unsafeRunSync()
  }

  test("malformed url") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    val req                             =
      Request[IO](Method.POST, uri"/register").withEntity(json"""{"url": "blah", "email": "someone@dbpedia.org"}""")
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body, Error.MalformedRegistrationRequest.asJson)
    test.unsafeRunSync()
  }

  test("invalid email") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    val req                             = Request[IO](Method.POST, uri"/register")
      .withEntity(json"""{"url": "http://dbpedia.org", "email": "blah"}""")
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body, Error.MalformedRegistrationRequest.asJson)
    test.unsafeRunSync()
  }

  test("endpoint check failed") {
    implicit val ioRepository           = new TestEndpointRepository[IO]
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new EndpointChecker[IO] {
      def check(ep: Endpoint): IO[Either[Error, Unit]] = IO(Left(Error.InvalidSparqlEndpoint(ep.url)))
    }
    implicit val encoder                = jsonEncoderOf[IO, Endpoint]
    val req                             = Request[IO](Method.POST, uri"/register").withEntity(TestEndpointRepository.testEndpoint)
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body, Error.InvalidSparqlEndpoint(TestEndpointRepository.testEndpoint.url).asJson)
    test.unsafeRunSync()
  }

  test("register duplicate") {
    implicit val ioRepository           = new EndpointRepository[IO] {
      def endpointsStream: fs2.Stream[IO, Endpoint]       = ???
      def endpoints: IO[Set[Endpoint]]                    = ???
      def register(ep: Endpoint): IO[Either[Error, Unit]] = IO(Left(Error.EndpointAlreadyRegistered(ep.url)))
    }
    implicit val ioRegistrationProducer = new TestRegistrationProducer[IO]
    implicit val ioEndpointChecker      = new TestEndpointChecker[IO]
    implicit val encoder                = jsonEncoderOf[IO, Endpoint]
    val req                             = Request[IO](Method.POST, uri"/register").withEntity(TestEndpointRepository.testEndpoint)
    val test                            = for {
      res  <- new Service[IO].routes.orNotFound.run(req)
      _    <- IO(assertEquals(res.status, Status.BadRequest))
      _    <- IO(assertEquals(res.httpVersion, HttpVersion.`HTTP/1.1`))
      body <- res.as[Json]
    } yield assertEquals(body, Error.EndpointAlreadyRegistered(TestEndpointRepository.testEndpoint.url).asJson)
    test.unsafeRunSync()
  }
}
