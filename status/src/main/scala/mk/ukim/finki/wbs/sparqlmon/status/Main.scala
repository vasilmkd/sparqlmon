package mk.ukim.finki.wbs.sparqlmon.status

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import cats.effect.{ Blocker, ExitCode, IO, IOApp }
import doobie.hikari.HikariTransactor
import fs2.kafka._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._

import mk.ukim.finki.wbs.sparqlmon.model._

object Main extends IOApp {

  private val resource = for {
    blocker <- Blocker[IO]
    xa      <- HikariTransactor.newHikariTransactor[IO](
            "org.postgresql.Driver",
            "jdbc:postgresql://postgres/postgres",
            "postgres",
            "password",
            ExecutionContext.global,
            blocker
          )
  } yield new PostgresStatusRepository[IO](xa)

  private val consumerSettings =
    ConsumerSettings[IO, String, EndpointAvailability]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("kafka:9092")
      .withGroupId("status")

  def run(args: List[String]): IO[ExitCode] =
    resource
      .flatMap { implicit sr =>
        BlazeServerBuilder[IO](ExecutionContext.global)
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(CORS(new Service[IO].routes).orNotFound)
          .resource
          .map(_ => sr)
      }
      .use { implicit sr =>
        consumerStream(consumerSettings)
          .evalTap(_.subscribeTo("availability"))
          .flatMap(_.stream)
          .parEvalMap(8) { commitable =>
            AvailabilityProcessor
              .processEndpointAvailability[IO](commitable.record.value)
              .as(commitable.offset)
          }
          .through(commitBatchWithin(100, 15.seconds))
          .compile
          .drain
      }
      .as(ExitCode.Success)
}
