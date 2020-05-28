package mk.ukim.finki.wbs.sparqlmon.status

import io.circe.{ Decoder, Encoder }
import io.circe.generic.auto._

sealed trait Error extends Product with Serializable {
  val error: String
}

object Error {
  final private case class JsonError(error: String)

  final case class MalformedUrlError(url: String) extends Error {
    override val error: String = s"Malformed url: $url"
  }

  object MalformedUrlError {
    implicit val encoder: Encoder[MalformedUrlError] =
      Encoder[JsonError].contramap[MalformedUrlError](e => JsonError(e.error))

    implicit val decoder: Decoder[MalformedUrlError] =
      Decoder[JsonError].map(je => MalformedUrlError(je.error.split(": ")(1)))
  }
}
