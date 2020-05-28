package mk.ukim.finki.wbs.sparqlmon.availability

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.model._

class TestAvailabilityRepository[F[_]: Applicative] extends AvailabilityRepository[F] {
  override def recordAvailability(ep: Endpoint, ar: AvailabilityRecord): F[Unit] =
    Applicative[F].unit
  override def availability(ep: Endpoint): F[Vector[AvailabilityRecord]]         =
    Applicative[F].pure(Vector.empty)
}
