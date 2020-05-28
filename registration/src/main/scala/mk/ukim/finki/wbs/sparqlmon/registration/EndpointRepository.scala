package mk.ukim.finki.wbs.sparqlmon.registration

import fs2.Stream

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

trait EndpointRepository[F[_]] {
  def endpointsStream: Stream[F, Endpoint]
  def endpoints: F[Set[Endpoint]]
  def register(ep: Endpoint): F[Either[Error, Unit]]
}

object EndpointRepository {
  def apply[F[_]: EndpointRepository]: EndpointRepository[F] = implicitly
}
