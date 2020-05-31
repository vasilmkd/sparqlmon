package mk.ukim.finki.wbs.sparqlmon.availability

import cats.effect.{ ConcurrentEffect, ContextShift }
import fs2.Stream
import fs2.kafka._

import mk.ukim.finki.wbs.sparqlmon.message.EndpointAvailability

class KafkaAvailabilityProducer[F[_]: ConcurrentEffect: ContextShift](
  ps: ProducerSettings[F, String, EndpointAvailability]
) extends AvailabilityProducer[F] {
  override def produceOne(ea: EndpointAvailability): F[Unit] =
    Stream
      .emit(ea)
      .map { ea =>
        ProducerRecords.one(ProducerRecord("availability", ea.endpoint.url.toString, ea))
      }
      .through(produce(ps))
      .compile
      .drain
}
