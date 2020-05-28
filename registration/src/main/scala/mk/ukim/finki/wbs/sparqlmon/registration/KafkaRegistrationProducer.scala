package mk.ukim.finki.wbs.sparqlmon.registration

import scala.concurrent.duration._

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import fs2.{ Pipe, Stream }
import fs2.kafka._

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class KafkaRegistrationProducer[F[_]: ConcurrentEffect: ContextShift: Timer: EndpointRepository](
  ps: ProducerSettings[F, String, Endpoint]
) extends RegistrationProducer[F] {
  override def produceOne(ep: Endpoint): F[Unit] =
    produceStream(Stream.emit(ep))

  override val produceRegistrations: F[Unit] =
    produceStream(EndpointRepository[F].endpointsStream.through(ticker))

  private def produceStream(stream: Stream[F, Endpoint]): F[Unit] =
    stream
      .map { ep =>
        ProducerRecords.one(ProducerRecord("registration", ep.url.toString, ep))
      }
      .through(produce(ps))
      .compile
      .drain

  private def ticker[A]: Pipe[F, A, A] =
    stream =>
      (Stream.emit(Duration.Zero) ++ Stream.awakeEvery[F](15.minutes))
        .flatMap(_ => stream)
}
