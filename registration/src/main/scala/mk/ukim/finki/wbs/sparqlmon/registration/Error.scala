package mk.ukim.finki.wbs.sparqlmon.registration

import java.net.URL

import io.circe.Encoder
import io.circe.generic.auto._

sealed trait Error extends Product with Serializable {
  val error: String
}

object Error {
  final private case class JsonError(error: String)

  implicit val encoder: Encoder[Error] =
    Encoder[JsonError].contramap[Error](e => JsonError(e.error))

  final case class EndpointAlreadyRegistered(url: URL) extends Error {
    override val error: String = s"Endpoint already registered: $url"
  }

  object EndpointAlreadyRegistered {
    implicit val encoder: Encoder[EndpointAlreadyRegistered] =
      Encoder[Error].contramap[EndpointAlreadyRegistered](_.asInstanceOf[Error])
  }

  final case class InvalidSparqlEndpoint(url: URL) extends Error {
    override val error: String = s"Invalid sparql endpoint: $url"
  }

  object InvalidSparqlEndpoint {
    implicit val encoder: Encoder[InvalidSparqlEndpoint] =
      Encoder[Error].contramap[InvalidSparqlEndpoint](_.asInstanceOf[Error])
  }

  case object MalformedRegistrationRequest extends Error {
    override val error: String = "Malformed endpoint registration request"

    implicit val encoder: Encoder[MalformedRegistrationRequest.type] =
      Encoder[Error].contramap[MalformedRegistrationRequest.type](_.asInstanceOf[Error])
  }
}
