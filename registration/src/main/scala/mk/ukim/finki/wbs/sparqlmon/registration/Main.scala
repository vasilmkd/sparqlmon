package mk.ukim.finki.wbs.sparqlmon.registration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import cats.effect.{ Blocker, ExitCode, IO, IOApp }
import doobie.hikari.HikariTransactor
import fs2.kafka.ProducerSettings
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

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
  } yield (new PostgresEndpointRepository[IO](xa), client)

  def run(args: List[String]): IO[ExitCode] =
    resource
      .flatMap {
        case (er, client) =>
          implicit val ec  = new HttpEndpointChecker[IO](client)
          implicit val ier = er
          implicit val rp  =
            new KafkaRegistrationProducer[IO](ProducerSettings[IO, String, Endpoint].withBootstrapServers("kafka:9092"))

          BlazeServerBuilder[IO](ExecutionContext.global)
            .bindHttp(8080, "0.0.0.0")
            .withHttpApp(CORS(new Service[IO].routes).orNotFound)
            .resource
            .map(_ => rp)
      }
      .use(_.produceRegistrations)
      .as(ExitCode.Success)
}
