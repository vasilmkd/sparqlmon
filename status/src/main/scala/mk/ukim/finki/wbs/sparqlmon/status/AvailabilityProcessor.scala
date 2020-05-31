package mk.ukim.finki.wbs.sparqlmon.status

import cats.effect.Sync

import mk.ukim.finki.wbs.sparqlmon.message.EndpointAvailability

object AvailabilityProcessor {
  def processEndpointAvailability[F[_]: StatusRepository: Sync](ea: EndpointAvailability): F[Unit] =
    StatusRepository[F].update(ea)
}
