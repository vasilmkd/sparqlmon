package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL

import mk.ukim.finki.wbs.sparqlmon.message._

trait AvailabilityRepository[F[_]] {
  def recordAvailability(url: URL, ar: AvailabilityRecord): F[Unit]
  def availability(url: URL): F[Vector[AvailabilityRecord]]
}

object AvailabilityRepository {
  def apply[F[_]: AvailabilityRepository]: AvailabilityRepository[F] = implicitly
}
