package mk.ukim.finki.wbs.sparqlmon.alerting

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

trait Alerter[F[_]] {
  def alert(ep: Endpoint): F[Unit]
}

object Alerter {
  def apply[F[_]: Alerter]: Alerter[F] = implicitly
}
