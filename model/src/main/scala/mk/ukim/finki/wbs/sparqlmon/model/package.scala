package mk.ukim.finki.wbs.sparqlmon

import java.net.URL
import java.sql.Timestamp
import java.time.{ Instant, LocalDateTime, ZoneOffset }

import cats.effect.Sync
import doobie.implicits.javasql.TimestampMeta
import doobie.util.meta.Meta
import fs2.kafka.{ Deserializer, Serializer }
import io.circe.{ Decoder, Encoder }

package object model {
  implicit val urlEncoder: Encoder[URL] =
    Encoder.encodeString.contramap[URL](_.toString)

  implicit val urlDecoder: Decoder[URL] =
    Decoder.decodeString.map(new URL(_))

  implicit def urlSerializer[F[_]: Sync]: Serializer[F, URL] =
    Serializer.string[F].contramap[URL](_.toString)

  implicit def urlDeserializer[F[_]: Sync]: Deserializer[F, URL] =
    Deserializer.string[F].map(new URL(_))

  implicit val instantMeta: Meta[Instant] =
    TimestampMeta.imap(_.toLocalDateTime.atZone(ZoneOffset.UTC).toInstant)(i =>
      Timestamp.valueOf(LocalDateTime.ofInstant(i, ZoneOffset.UTC))
    )
}
