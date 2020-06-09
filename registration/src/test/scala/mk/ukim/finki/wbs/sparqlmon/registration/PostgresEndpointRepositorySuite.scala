package mk.ukim.finki.wbs.sparqlmon.registration

import java.net.URL
import javax.mail.internet.InternetAddress

import scala.concurrent.ExecutionContext

import cats.data.EitherT
import cats.effect.{ Blocker, IO }
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

class PostgresEndpointRepositorySuite extends FunSuite {

  implicit private val contextShift = IO.contextShift(ExecutionContext.global)

  implicit private val timer = IO.timer(ExecutionContext.global)

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
    transactor.map(new PostgresEndpointRepository[IO](_))

  override def afterAll(): Unit =
    transactor
      .use { xa =>
        sql"delete from endpoint".update.run.transact(xa).void
      }
      .unsafeRunSync()

  test("insert and read test") {
    val expected = Set(
      Endpoint(new URL("http://dbpedia.org/sparql"), Some(new InternetAddress("someone@dbpedia.org"))),
      Endpoint(new URL("https://query.wikidata.org/sparql"), Some(new InternetAddress("someone@wikidata.org")))
    )

    val test = repo.use { repo =>
      for {
        _   <- expected.toList.traverse_(ep => EitherT(repo.register(ep))).getOrElse(())
        eps <- repo.endpoints
      } yield assertEquals(eps, expected)
    }
    test.unsafeRunSync()
  }

  test("insert duplicate") {
    val url  = new URL("http://dbpedia.org/sparql")
    val test = repo.use(_.register(Endpoint(url, Some(new InternetAddress("someone@dbpedia.org")))))
    assertEquals(test.unsafeRunSync(), Left(Error.EndpointAlreadyRegistered(url)))
  }
}
