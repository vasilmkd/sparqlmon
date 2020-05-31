package mk.ukim.finki.wbs.sparqlmon.alerting

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

class TestAlerter[F[_]: Applicative] extends Alerter[F] {
  override def alert(ep: Endpoint): F[Unit] =
    Applicative[F].unit
}
