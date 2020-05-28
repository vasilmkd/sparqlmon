package mk.ukim.finki.wbs.sparqlmon.registration

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

trait EndpointChecker[F[_]] {
  def check(ep: Endpoint): F[Either[Error, Unit]]
}

object EndpointChecker {
  def apply[F[_]: EndpointChecker]: EndpointChecker[F] = implicitly
}
