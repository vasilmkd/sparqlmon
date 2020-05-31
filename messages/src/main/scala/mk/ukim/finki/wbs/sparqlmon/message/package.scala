package mk.ukim.finki.wbs.sparqlmon

import java.net.URL
import java.sql.Timestamp
import java.time.{ Instant, LocalDateTime, ZoneOffset }
import javax.mail.internet.InternetAddress

import doobie.implicits.javasql.TimestampMeta
import doobie.util.meta.Meta
import io.circe.{ Decoder, Encoder }

package object message {
  implicit val urlEncoder: Encoder[URL] =
    Encoder.encodeString.contramap[URL](_.toString)

  implicit val urlDecoder: Decoder[URL] =
    Decoder.decodeString.map(new URL(_))

  implicit val internetAddressEncoder: Encoder[InternetAddress] =
    Encoder.encodeString.contramap[InternetAddress](_.toString)

  implicit val internetAddressDecoder: Decoder[InternetAddress] =
    Decoder.decodeString.map(new InternetAddress(_))

  implicit val instantMeta: Meta[Instant] =
    TimestampMeta.imap(_.toLocalDateTime.atZone(ZoneOffset.UTC).toInstant)(i =>
      Timestamp.valueOf(LocalDateTime.ofInstant(i, ZoneOffset.UTC))
    )
}
