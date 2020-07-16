package mk.ukim.finki.wbs.sparqlmon.availability

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

trait QueryMaker[F[_]] {
  def ask(ep: Endpoint): F[Option[Unit]]
  def select(ep: Endpoint): F[Option[Unit]]
}

object QueryMaker {
  def apply[F[_]: QueryMaker]: QueryMaker[F] = implicitly
}
