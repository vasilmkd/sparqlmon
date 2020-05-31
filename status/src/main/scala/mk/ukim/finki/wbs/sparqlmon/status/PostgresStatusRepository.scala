package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL
import java.time.Instant

import cats.effect.Async
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor

import mk.ukim.finki.wbs.sparqlmon.message._

class PostgresStatusRepository[F[_]: Async](xa: Transactor[F]) extends StatusRepository[F] {
  override def update(ea: EndpointAvailability): F[Unit] =
    sql"""insert into status (url, instant, up)
          values (${ea.endpoint.url.toString}, ${ea.record.instant}, ${ea.record.up})
          on conflict (url)
          do update set instant = ${ea.record.instant}, up = ${ea.record.up} where status.url = ${ea.endpoint.url.toString}""".update.run
      .transact(xa)
      .void

  override def status(url: URL): F[Option[AvailabilityRecord]] =
    sql"select instant, up from status where url = ${url.toString}"
      .query[(Instant, Boolean)]
      .stream
      .map(t => AvailabilityRecord(t._1, t._2))
      .compile
      .last
      .transact(xa)

  override val overview: F[Vector[Status]] =
    sql"select url, instant, up from status"
      .query[(String, Instant, Boolean)]
      .stream
      .map(t => Status(new URL(t._1), Some(AvailabilityRecord(t._2, t._3))))
      .compile
      .toVector
      .transact(xa)
}
