package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL
import java.time.Instant

import scala.concurrent.ExecutionContext

import cats.effect.{ Blocker, IO }
import doobie.implicits._
import doobie.util.transactor.Transactor
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.model._

class PostgresStatusRepositorySuite extends FunSuite {

  implicit private val contextShift = IO.contextShift(ExecutionContext.global)

  private val transactor = Blocker[IO]
    .map { blocker =>
      Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql:postgres",
        "postgres",
        "testpassword",
        blocker
      )
    }

  private val repo =
    transactor.map(new PostgresStatusRepository[IO](_))

  override def afterAll(): Unit =
    transactor
      .use { xa =>
        sql"delete from status".update.run.transact(xa).void
      }
      .unsafeRunSync()

  test("insert and read test") {
    val expected = EndpointAvailability(
      Endpoint(new URL("http://dbpedia.org/sparql")),
      AvailabilityRecord(
        Instant.ofEpochMilli(1),
        true
      )
    )

    val test = repo.use { repo =>
      for {
        _        <- repo.update(expected)
        status   <- repo.status(expected.endpoint)
        overview <- repo.overview
        _        <- IO(assertEquals(status, Some(expected.record)))
      } yield assertEquals(overview, Vector(Status(expected.endpoint.url, Some(expected.record))))
    }
    test.unsafeRunSync()
  }
}
