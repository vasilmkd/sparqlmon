package mk.ukim.finki.wbs.sparqlmon.registration

import java.net.URL
import java.time.Instant
import javax.mail.internet.InternetAddress

import scala.concurrent.duration.MILLISECONDS

import cats.Applicative
import cats.effect.{ Async, ContextShift, Timer }
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream
import org.postgresql.util.PSQLException

import mk.ukim.finki.wbs.sparqlmon.message._

class PostgresEndpointRepository[F[_]: Async: ContextShift: Timer](xa: Transactor[F]) extends EndpointRepository[F] {

  override def endpointsStream: Stream[F, Endpoint] =
    sql"select url, email from endpoint"
      .query[(String, Option[String])]
      .stream
      .map(t => Endpoint(new URL(t._1), t._2.map(new InternetAddress(_))))
      .transact(xa)

  override def endpoints: F[Set[Endpoint]] =
    endpointsStream.compile
      .fold(Set.empty[Endpoint])(_ + _)

  override def register(ep: Endpoint): F[Either[Error, Unit]] =
    for {
      timestamp <- Timer[F].clock.realTime(MILLISECONDS)
      res       <- sql"insert into endpoint (url, registered, email) values (${ep.url.show}, ${Instant
               .ofEpochMilli(timestamp)}, ${ep.email.map(_.show)})".update.run
               .transact(xa)
               .as(Either.right[Error, Unit](()))
               .recoverWith {
                 case e: PSQLException if e.getMessage.contains("duplicate key value violates unique constraint") =>
                   Applicative[F].pure(Either.left[Error, Unit](Error.EndpointAlreadyRegistered(ep.url)))
               }
    } yield res
}
