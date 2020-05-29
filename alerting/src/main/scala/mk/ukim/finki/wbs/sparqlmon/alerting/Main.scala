package mk.ukim.finki.wbs.sparqlmon.alerting

import scala.concurrent.duration._

import cats.effect.{ ExitCode, IO, IOApp }
import cats.implicits._
import fs2.kafka._

import mk.ukim.finki.wbs.sparqlmon.model._

object Main extends IOApp {

  private val consumerSettings =
    ConsumerSettings[IO, String, EndpointAvailability]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("kafka:9092")
      .withGroupId("alerting")

  implicit private val ea = new EmailAlerter[IO]

  def run(args: List[String]): IO[ExitCode] =
    consumerStream(consumerSettings)
      .evalTap(_.subscribeTo("availability"))
      .flatMap(_.stream)
      .evalMapAccumulate(AlertingState.empty) { (state, commitable) =>
        AvailabilityProcessor
          .processEndpointAvailability[IO](commitable.record.value)
          .run(state)
          .map(t => (t._1, commitable.offset))
      }
      .map(_._2)
      .through(commitBatchWithin(100, 15.seconds))
      .compile
      .drain
      .as(ExitCode.Success)
}
