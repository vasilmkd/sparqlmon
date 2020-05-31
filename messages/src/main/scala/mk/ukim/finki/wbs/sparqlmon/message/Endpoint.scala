package mk.ukim.finki.wbs.sparqlmon.message

import java.net.URL
import javax.mail.internet.InternetAddress

import cats.effect.Sync
import fs2.kafka.{ Deserializer, Serializer }
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

final case class Endpoint(url: URL, email: InternetAddress)

object Endpoint {
  implicit def serializer[F[_]: Sync]: Serializer[F, Endpoint] =
    Serializer.string[F].contramap[Endpoint](_.asJson.toString)

  implicit def deserializer[F[_]: Sync]: Deserializer[F, Endpoint] =
    Deserializer.string[F].map(decode[Endpoint](_).toOption.get)
}
