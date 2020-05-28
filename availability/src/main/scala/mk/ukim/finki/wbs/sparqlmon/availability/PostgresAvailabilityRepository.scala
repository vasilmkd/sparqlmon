package mk.ukim.finki.wbs.sparqlmon.availability

import java.time.Instant

import cats.effect.Async
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor

import mk.ukim.finki.wbs.sparqlmon.model._

class PostgresAvailabilityRepository[F[_]: Async](xa: Transactor[F]) extends AvailabilityRepository[F] {

  override def recordAvailability(ep: Endpoint, ar: AvailabilityRecord): F[Unit] =
    sql"insert into availability (url, instant, up) values (${ep.url.toString}, ${ar.instant.toString} :: timestamp, ${ar.up})".update.run
      .transact(xa)
      .void

  override def availability(ep: Endpoint): F[Vector[AvailabilityRecord]] =
    sql"select instant, up from availability where url = ${ep.url.toString}"
      .query[(Instant, Boolean)]
      .stream
      .map(t => AvailabilityRecord(t._1, t._2))
      .compile
      .toVector
      .transact(xa)
}
