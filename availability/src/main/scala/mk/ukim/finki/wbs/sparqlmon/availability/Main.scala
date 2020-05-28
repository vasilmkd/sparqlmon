package mk.ukim.finki.wbs.sparqlmon.availability

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import cats.effect.{ Blocker, ExitCode, IO, IOApp }
import doobie.hikari.HikariTransactor
import fs2.kafka._
import org.http4s.client.blaze.BlazeClientBuilder
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
    client  <- BlazeClientBuilder[IO](ExecutionContext.global)
                .withRequestTimeout(1.minute)
                .resource
  } yield (new PostgresAvailabilityRepository[IO](xa), client)

  private val consumerSettings =
    ConsumerSettings[IO, String, Endpoint]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("kafka:9092")
      .withGroupId("availability")

  def run(args: List[String]): IO[ExitCode] =
    resource
      .flatMap {
        case (ar, client) =>
          implicit val iar = ar
          BlazeServerBuilder[IO](ExecutionContext.global)
            .bindHttp(8080, "0.0.0.0")
            .withHttpApp(CORS(new Service[IO].routes).orNotFound)
            .resource
            .map(_ => (iar, client))
      }
      .use {
        case (ar, client) =>
          implicit val iar = ar
          implicit val qm  = new HttpQueryMaker[IO](client)
          implicit val ap  = new KafkaAvailabilityProducer[IO](
            ProducerSettings[IO, String, EndpointAvailability].withBootstrapServers("kafka:9092")
          )

          consumerStream(consumerSettings)
            .evalTap(_.subscribeTo("registration"))
            .flatMap(_.stream)
            .parEvalMap(8) { commitable =>
              RegistrationProcessor
                .processEndpoint[IO](commitable.record.value)
                .as(commitable.offset)
            }
            .through(commitBatchWithin(100, 15.seconds))
            .compile
            .drain
      }
      .as(ExitCode.Success)
}