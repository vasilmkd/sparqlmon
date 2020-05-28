package mk.ukim.finki.wbs.sparqlmon.registration

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class TestEndpointChecker[F[_]: Applicative] extends EndpointChecker[F] {
  override def check(ep: Endpoint): F[Either[Error, Unit]] = Applicative[F].pure(Right(()))
}
