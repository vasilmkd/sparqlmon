package mk.ukim.finki.wbs.sparqlmon.availability

import cats.effect.{ Sync, Timer }
import cats.implicits._

import mk.ukim.finki.wbs.sparqlmon.model._

object RegistrationProcessor {

  def processEndpoint[F[_]: Sync: Timer: QueryMaker: AvailabilityRepository: AvailabilityProducer](
    ep: Endpoint
  ): F[Unit] =
    for {
      record <- AvailabilityChecker.checkAvailability(ep)
      _      <- AvailabilityRepository[F].recordAvailability(ep, record)
      _      <- AvailabilityProducer[F].produceOne(EndpointAvailability(ep, record))
    } yield ()
}
