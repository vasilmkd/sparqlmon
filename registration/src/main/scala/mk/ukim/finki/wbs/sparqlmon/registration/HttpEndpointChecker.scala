package mk.ukim.finki.wbs.sparqlmon.registration

import cats.effect.Sync
import cats.implicits._
import org.http4s.{ Header, Method, Request, Uri }
import org.http4s.circe._
import org.http4s.client.Client

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class HttpEndpointChecker[F[_]: Sync](client: Client[F]) extends EndpointChecker[F] {

  private val query  = "select ?s where { ?s ?p ?o . } limit 1"
  private val format = "application/sparql-results+json"

  override def check(ep: Endpoint): F[Either[Error, Unit]] = {
    val uri = Uri.unsafeFromString(ep.url.toString).withQueryParam("query", query)
    val req = Request[F](Method.GET, uri).putHeaders(Header("Accept", format))
    client
      .fetch(req)(ResponseChecker.checkResponse(_))
      .handleError(_ => None)
      .map(Either.fromOption(_, Error.InvalidSparqlEndpoint(ep.url)))
  }
}
