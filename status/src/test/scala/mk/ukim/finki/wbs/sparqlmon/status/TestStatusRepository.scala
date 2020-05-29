package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.model._

class TestStatusRepository[F[_]: Applicative] extends StatusRepository[F] {
  override def update(ea: EndpointAvailability): F[Unit]       =
    Applicative[F].unit
  override def status(url: URL): F[Option[AvailabilityRecord]] =
    Applicative[F].pure(None)
  override val overview: F[Vector[Status]]                     =
    Applicative[F].pure(Vector.empty)
}
