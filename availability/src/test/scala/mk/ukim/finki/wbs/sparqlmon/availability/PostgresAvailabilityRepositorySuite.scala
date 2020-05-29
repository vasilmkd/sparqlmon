package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL
import java.time.Instant

import scala.concurrent.ExecutionContext

import cats.effect.{ Blocker, IO }
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.model._

class PostgresAvailabilityRepositorySuite extends FunSuite {

  implicit private val contextShift = IO.contextShift(ExecutionContext.global)

  private val transactor = Blocker[IO]
    .map { blocker =>
      Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql:sparqlmontest",
        "sparqlmontest",
        "sparqlmontestpassword",
        blocker
      )
    }

  private val repo =
    transactor.map(new PostgresAvailabilityRepository[IO](_))

  override def afterAll(): Unit =
    transactor
      .use { xa =>
        sql"delete from availability".update.run.transact(xa).void
      }
      .unsafeRunSync()

  test("insert and read test") {
    val endpoint = new URL("http://dbpedia.org/sparql")

    val expected = Vector(
      AvailabilityRecord(Instant.ofEpochMilli(1), true),
      AvailabilityRecord(Instant.ofEpochMilli(2), false),
      AvailabilityRecord(Instant.ofEpochMilli(3), true)
    )

    val test = repo.use { repo =>
      for {
        _       <- expected.toList.traverse_(ar => repo.recordAvailability(endpoint, ar))
        history <- repo.availability(endpoint)
      } yield assertEquals(history, expected)
    }
    test.unsafeRunSync()
  }
}
