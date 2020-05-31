package mk.ukim.finki.wbs.sparqlmon.availability

import cats.data.OptionT

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

trait QueryMaker[F[_]] {
  def ask(ep: Endpoint): OptionT[F, Unit]
  def select(ep: Endpoint): OptionT[F, Unit]
}

object QueryMaker {
  def apply[F[_]: QueryMaker]: QueryMaker[F] = implicitly
}
