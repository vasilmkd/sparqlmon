package mk.ukim.finki.wbs.sparqlmon.availability

import mk.ukim.finki.wbs.sparqlmon.model._

trait AvailabilityRepository[F[_]] {
  def recordAvailability(ep: Endpoint, ar: AvailabilityRecord): F[Unit]
  def availability(ep: Endpoint): F[Vector[AvailabilityRecord]]
}

object AvailabilityRepository {
  def apply[F[_]: AvailabilityRepository]: AvailabilityRepository[F] = implicitly
}
