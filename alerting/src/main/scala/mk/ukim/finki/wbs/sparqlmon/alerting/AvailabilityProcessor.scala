package mk.ukim.finki.wbs.sparqlmon.alerting

import cats.data.StateT
import cats.effect.Sync
import cats.implicits._

import mk.ukim.finki.wbs.sparqlmon.message.EndpointAvailability

object AvailabilityProcessor {
  def processEndpointAvailability[F[_]: Sync: Alerter](ea: EndpointAvailability): StateT[F, AlertingState, Unit] =
    StateT { state =>
      val counter = state.get(ea.endpoint).getOrElse(0)
      val next    = if (ea.record.up) 0 else counter + 1
      (if (next === 4)
         Alerter[F].alert(ea.endpoint)
       else
         Sync[F].unit)
        .map { _ =>
          (state + (ea.endpoint -> next % 4), ())
        }
    }
}
