package mk.ukim.finki.wbs.sparqlmon.availability

import cats.Applicative
import cats.data.OptionT

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class TestQueryMaker[F[_]: Applicative] extends QueryMaker[F] {
  override def ask(ep: Endpoint): OptionT[F, Unit]    = OptionT.some[F](())
  override def select(ep: Endpoint): OptionT[F, Unit] = OptionT.some[F](())
}
