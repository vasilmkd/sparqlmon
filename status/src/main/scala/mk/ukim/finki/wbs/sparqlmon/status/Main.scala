package mk.ukim.finki.wbs.sparqlmon.status

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import cats.effect.{ Blocker, ExitCode, IO, IOApp, Resource }
import doobie.hikari.HikariTransactor
import fs2.kafka._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import mk.ukim.finki.wbs.sparqlmon.message._

object Main extends IOApp {

  private val resource = for {
    blocker  <- Blocker[IO]
    password <- Resource.make(IO(System.getenv("SPARQLMON_PASSWORD")))(_ => IO.unit)
    xa       <- HikariTransactor.newHikariTransactor[IO](
            "org.postgresql.Driver",
            "jdbc:postgresql://postgres/sparqlmon",
            "sparqlmon",
            password,
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
          .withHttpApp(new Service[IO].routes.orNotFound)
          .resource
          .map(_ => sr)
      }
      .use { implicit sr =>
        for {
          cpus <- IO(Runtime.getRuntime().availableProcessors())
          _    <- consumerStream(consumerSettings)
                 .evalTap(_.subscribeTo("availability"))
                 .flatMap(_.stream)
                 .parEvalMap(cpus) { commitable =>
                   AvailabilityProcessor
                     .processEndpointAvailability[IO](commitable.record.value)
                     .as(commitable.offset)
                 }
                 .through(commitBatchWithin(100, 15.seconds))
                 .compile
                 .drain
        } yield ()
      }
      .as(ExitCode.Success)
}
