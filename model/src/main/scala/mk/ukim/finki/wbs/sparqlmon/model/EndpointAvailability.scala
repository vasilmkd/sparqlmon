package mk.ukim.finki.wbs.sparqlmon.model

import cats.effect.Sync
import fs2.kafka.{ Deserializer, Serializer }
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

final case class EndpointAvailability(endpoint: Endpoint, record: AvailabilityRecord)

object EndpointAvailability {
  implicit def serializer[F[_]: Sync]: Serializer[F, EndpointAvailability] =
    Serializer.string[F].contramap[EndpointAvailability](_.asJson.toString)

  implicit def deserializer[F[_]: Sync]: Deserializer[F, EndpointAvailability] =
    Deserializer.string[F].map(decode[EndpointAvailability](_).toOption.get)
}
