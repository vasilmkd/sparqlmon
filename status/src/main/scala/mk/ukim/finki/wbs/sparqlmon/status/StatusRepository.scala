package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL

import mk.ukim.finki.wbs.sparqlmon.message._

trait StatusRepository[F[_]] {
  def update(ea: EndpointAvailability): F[Unit]
  def status(url: URL): F[Option[AvailabilityRecord]]
  val overview: F[Vector[Status]]
}

object StatusRepository {
  def apply[F[_]: StatusRepository]: StatusRepository[F] = implicitly
}
