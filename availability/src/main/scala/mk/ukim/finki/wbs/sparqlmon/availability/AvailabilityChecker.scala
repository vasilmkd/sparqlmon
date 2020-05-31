package mk.ukim.finki.wbs.sparqlmon.availability

import java.time.Instant

import scala.concurrent.duration._

import cats.effect.{ Sync, Timer }
import cats.implicits._

import mk.ukim.finki.wbs.sparqlmon.message._

object AvailabilityChecker {

  def checkAvailability[F[_]: Sync: Timer: QueryMaker](ep: Endpoint): F[AvailabilityRecord] =
    QueryMaker[F]
      .ask(ep)
      .orElse(QueryMaker[F].select(ep))
      .value
      .map(_.isDefined)
      .flatMap { up =>
        for {
          timestamp <- Timer[F].clock.realTime(MILLISECONDS)
          instant    = Instant.ofEpochMilli(timestamp)
        } yield AvailabilityRecord(instant, up)
      }
}
