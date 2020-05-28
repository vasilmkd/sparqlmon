package mk.ukim.finki.wbs.sparqlmon.registration

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class TestRegistrationProducer[F[_]: Applicative] extends RegistrationProducer[F] {
  override def produceOne(ep: Endpoint): F[Unit] = Applicative[F].unit
  override val produceRegistrations: F[Unit]     = Applicative[F].unit
}
