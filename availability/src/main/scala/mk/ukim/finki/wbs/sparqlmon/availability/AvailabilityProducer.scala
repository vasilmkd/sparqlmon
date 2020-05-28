package mk.ukim.finki.wbs.sparqlmon.availability

import mk.ukim.finki.wbs.sparqlmon.model.EndpointAvailability

trait AvailabilityProducer[F[_]] {
  def produceOne(ea: EndpointAvailability): F[Unit]
}

object AvailabilityProducer {
  def apply[F[_]: AvailabilityProducer]: AvailabilityProducer[F] = implicitly
}
