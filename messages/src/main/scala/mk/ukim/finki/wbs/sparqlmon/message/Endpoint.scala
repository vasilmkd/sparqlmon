package mk.ukim.finki.wbs.sparqlmon.message

import java.net.URL
import javax.mail.internet.InternetAddress

import cats.effect.Sync
import cats.implicits._
import fs2.kafka.{ Deserializer, Serializer }
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

final case class Endpoint(url: URL, email: Option[InternetAddress])

object Endpoint {
  implicit def serializer[F[_]: Sync]: Serializer[F, Endpoint] =
    Serializer.string[F].contramap[Endpoint](_.asJson.show)

  implicit def deserializer[F[_]: Sync]: Deserializer[F, Endpoint] =
    Deserializer.string[F].map(decode[Endpoint](_).toOption.get)
}
