package mk.ukim.finki.wbs.sparqlmon

import java.net.URL
import java.sql.Timestamp
import java.time.{ Instant, LocalDateTime, ZoneOffset }
import javax.mail.internet.InternetAddress

import cats.Show
import cats.implicits._
import doobie.implicits.javasql.TimestampMeta
import doobie.util.meta.Meta
import io.circe.{ Decoder, Encoder }

package object message {
  implicit val urlShow: Show[URL] =
    Show.fromToString

  implicit val urlEncoder: Encoder[URL] =
    Encoder.encodeString.contramap[URL](_.show)

  implicit val urlDecoder: Decoder[URL] =
    Decoder.decodeString.map(new URL(_))

  implicit val internedAddressShow: Show[InternetAddress] =
    Show.fromToString

  implicit val internetAddressEncoder: Encoder[InternetAddress] =
    Encoder.encodeString.contramap[InternetAddress](_.show)

  implicit val internetAddressDecoder: Decoder[InternetAddress] =
    Decoder.decodeString.map(new InternetAddress(_))

  implicit val instantShow: Show[Instant] =
    Show.fromToString

  implicit val instantMeta: Meta[Instant] =
    TimestampMeta.imap(_.toLocalDateTime.atZone(ZoneOffset.UTC).toInstant)(i =>
      Timestamp.valueOf(LocalDateTime.ofInstant(i, ZoneOffset.UTC))
    )
}
