package mk.ukim.finki.wbs.sparqlmon.status

import mk.ukim.finki.wbs.sparqlmon.model._

trait StatusRepository[F[_]] {
  def update(ea: EndpointAvailability): F[Unit]
  def status(ep: Endpoint): F[Option[AvailabilityRecord]]
  val overview: F[Vector[Status]]
}

object StatusRepository {
  def apply[F[_]: StatusRepository]: StatusRepository[F] = implicitly
}
