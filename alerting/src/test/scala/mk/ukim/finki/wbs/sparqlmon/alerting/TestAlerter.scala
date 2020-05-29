package mk.ukim.finki.wbs.sparqlmon.alerting

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class TestAlerter[F[_]: Applicative] extends Alerter[F] {
  override def alert(ep: Endpoint): F[Unit] =
    Applicative[F].unit
}
