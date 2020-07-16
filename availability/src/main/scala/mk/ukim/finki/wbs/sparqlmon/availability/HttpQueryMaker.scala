package mk.ukim.finki.wbs.sparqlmon.availability

import cats.effect.Sync
import cats.implicits._
import org.http4s.{ Header, Method, Request, Uri }
import org.http4s.circe._
import org.http4s.client.Client

import mk.ukim.finki.wbs.sparqlmon.message._

class HttpQueryMaker[F[_]: Sync](client: Client[F]) extends QueryMaker[F] {

  private val askQuery    = "ask where { ?s ?p ?o . }"
  private val selectQuery = "select ?s where { ?s ?p ?o . } limit 1"
  private val format      = "application/sparql-results+json"

  override def ask(ep: Endpoint): F[Option[Unit]] = {
    val uri = Uri.unsafeFromString(ep.url.show).withQueryParam("query", askQuery)
    val req = Request[F](Method.GET, uri).putHeaders(Header("Accept", format))
    client
      .run(req)
      .use(ResponseChecker.checkAskResponse(_))
      .handleError(_ => None)
  }

  override def select(ep: Endpoint): F[Option[Unit]] = {
    val uri = Uri.unsafeFromString(ep.url.show).withQueryParam("query", selectQuery)
    val req = Request[F](Method.GET, uri).putHeaders(Header("Accept", format))
    client
      .run(req)
      .use(ResponseChecker.checkSelectResponse(_))
      .handleError(_ => None)
  }
}
