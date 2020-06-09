package mk.ukim.finki.wbs.sparqlmon.availability

import cats.effect.{ ConcurrentEffect, ContextShift }
import cats.implicits._
import fs2.Stream
import fs2.kafka._

import mk.ukim.finki.wbs.sparqlmon.message._

class KafkaAvailabilityProducer[F[_]: ConcurrentEffect: ContextShift](
  ps: ProducerSettings[F, String, EndpointAvailability]
) extends AvailabilityProducer[F] {
  override def produceOne(ea: EndpointAvailability): F[Unit] =
    Stream
      .emit(ea)
      .map { ea =>
        ProducerRecords.one(ProducerRecord("availability", ea.endpoint.url.show, ea))
      }
      .through(produce(ps))
      .compile
      .drain
}
