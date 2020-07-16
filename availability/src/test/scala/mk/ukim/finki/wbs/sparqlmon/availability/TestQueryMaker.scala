package mk.ukim.finki.wbs.sparqlmon.availability

import cats.Applicative

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

class TestQueryMaker[F[_]: Applicative] extends QueryMaker[F] {
  override def ask(ep: Endpoint): F[Option[Unit]]    = Applicative[F].pure(Some(()))
  override def select(ep: Endpoint): F[Option[Unit]] = Applicative[F].pure(Some(()))
}
