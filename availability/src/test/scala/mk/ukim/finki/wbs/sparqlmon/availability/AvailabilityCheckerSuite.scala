package mk.ukim.finki.wbs.sparqlmon.availability

import scala.concurrent.duration._

import java.net.URL
import javax.mail.internet.InternetAddress

import cats.data.OptionT
import cats.effect.{ Clock, IO, Timer }
import cats.implicits._
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.message._

class AvailabilityCheckerSuite extends FunSuite {

  private val endpoint = Endpoint(new URL("http://dbpedia.org/sparql"), new InternetAddress("someone@dbpedia.org"))

  implicit private val timer = new Timer[IO] {
    def clock: Clock[IO]                          =
      new Clock[IO] {
        def realTime(unit: concurrent.duration.TimeUnit): IO[Long]  = IO.pure(1609L)
        def monotonic(unit: concurrent.duration.TimeUnit): IO[Long] = IO.pure(1610L)
      }
    def sleep(duration: FiniteDuration): IO[Unit] = IO.unit
  }

  test("check availability down") {
    implicit val qm = new QueryMaker[IO] {
      def ask(ep: Endpoint)    = OptionT.none[IO, Unit]
      def select(ep: Endpoint) = OptionT.none[IO, Unit]
    }
    val test        = for {
      record <- AvailabilityChecker.checkAvailability[IO](endpoint)
      _      <- IO(assert(record.instant.toEpochMilli === 1609L))
    } yield assertEquals(record.up, false)
    test.unsafeRunSync()
  }

  test("check availability ask works") {
    implicit val qm = new QueryMaker[IO] {
      def ask(ep: Endpoint)    = OptionT.some[IO](())
      def select(ep: Endpoint) = OptionT.none[IO, Unit]
    }
    val test        = for {
      record <- AvailabilityChecker.checkAvailability[IO](endpoint)
      _      <- IO(assert(record.instant.toEpochMilli === 1609L))
    } yield assertEquals(record.up, true)
    test.unsafeRunSync()
  }

  test("check availability select works") {
    implicit val qm = new QueryMaker[IO] {
      def ask(ep: Endpoint)    = OptionT.none[IO, Unit]
      def select(ep: Endpoint) = OptionT.some[IO](())
    }
    val test        = for {
      record <- AvailabilityChecker.checkAvailability[IO](endpoint)
      _      <- IO(assert(record.instant.toEpochMilli === 1609L))
    } yield assertEquals(record.up, true)
    test.unsafeRunSync()
  }

  test("check availability") {
    implicit val qm = new TestQueryMaker[IO]
    val test        = for {
      record <- AvailabilityChecker.checkAvailability[IO](endpoint)
      _      <- IO(assert(record.instant.toEpochMilli === 1609L))
    } yield assertEquals(record.up, true)
    test.unsafeRunSync()
  }
}
