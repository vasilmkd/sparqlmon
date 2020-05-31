package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.message._

class TestAvailabilityRepository[F[_]: Applicative] extends AvailabilityRepository[F] {
  override def recordAvailability(url: URL, ar: AvailabilityRecord): F[Unit] =
    Applicative[F].unit
  override def availability(url: URL): F[Vector[AvailabilityRecord]]         =
    Applicative[F].pure(Vector.empty)
}
