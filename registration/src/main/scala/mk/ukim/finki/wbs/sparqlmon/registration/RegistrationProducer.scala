package mk.ukim.finki.wbs.sparqlmon.registration

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

trait RegistrationProducer[F[_]] {
  def produceOne(ep: Endpoint): F[Unit]
  def produceRegistrations: F[Unit]
}

object RegistrationProducer {
  def apply[F[_]: RegistrationProducer]: RegistrationProducer[F] = implicitly
}
