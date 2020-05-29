package mk.ukim.finki.wbs.sparqlmon.gateway

import scala.concurrent.ExecutionContext

import cats.effect.{ ExitCode, IO, IOApp }
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._

object Main extends IOApp {

  private val resource = for {
    client <- BlazeClientBuilder[IO](ExecutionContext.global).resource
    server <- BlazeServerBuilder[IO](ExecutionContext.global)
                .bindHttp(8080, "0.0.0.0")
                .withHttpApp(CORS(new Service[IO](client).routes).orNotFound)
                .resource
  } yield server

  def run(args: List[String]): IO[ExitCode] =
    resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
