package mk.ukim.finki.wbs.sparqlmon.model

import java.net.URL

import cats.effect.Sync
import fs2.kafka.{ Deserializer, Serializer }

final case class Endpoint(url: URL) extends AnyVal

object Endpoint {
  implicit def serializer[F[_]: Sync]: Serializer[F, Endpoint] =
    Serializer[F, URL].contramap[Endpoint](_.url)

  implicit def deserializer[F[_]: Sync]: Deserializer[F, Endpoint] =
    Deserializer[F, URL].map(Endpoint(_))
}
