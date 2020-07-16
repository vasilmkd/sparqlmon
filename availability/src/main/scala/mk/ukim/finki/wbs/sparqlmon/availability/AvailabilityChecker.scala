package mk.ukim.finki.wbs.sparqlmon.availability

import java.time.Instant

import scala.concurrent.duration._

import cats.data.OptionT
import cats.effect.{ Sync, Timer }
import cats.implicits._

import mk.ukim.finki.wbs.sparqlmon.message._

object AvailabilityChecker {

  def checkAvailability[F[_]: Sync: Timer: QueryMaker](ep: Endpoint): F[AvailabilityRecord] =
    OptionT(QueryMaker[F].ask(ep))
      .orElse(OptionT(QueryMaker[F].select(ep)))
      .isDefined
      .flatMap { up =>
        for {
          timestamp <- Timer[F].clock.realTime(MILLISECONDS)
          instant    = Instant.ofEpochMilli(timestamp)
        } yield AvailabilityRecord(instant, up)
      }
}
