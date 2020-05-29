package mk.ukim.finki.wbs.sparqlmon.gateway

import cats.Applicative
import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._

class Service[F[_]: Sync](client: Client[F]) extends Http4sDsl[F] {

  object UrlParamDecoderMatcher extends QueryParamDecoderMatcher[String]("url")

  val routes: HttpRoutes[F] = {
    val _ = client
    HttpRoutes.of[F] {
      case req @ GET -> Root / "endpoints"                                   =>
        client.fetch(req.withUri(uri"http://registration:8080/endpoints"))(Applicative[F].pure(_))

      case req @ POST -> Root / "register"                                   =>
        client.fetch(req.withUri(uri"http://registration:8080/register"))(Applicative[F].pure(_))

      case req @ GET -> Root / "availability" :? UrlParamDecoderMatcher(url) =>
        client.fetch(req.withUri(uri"http://availability:8080/availability".withQueryParam("url", url)))(
          Applicative[F].pure(_)
        )

      case req @ GET -> Root / "status" :? UrlParamDecoderMatcher(url)       =>
        client.fetch(req.withUri(uri"http://status:8080/status".withQueryParam("url", url)))(Applicative[F].pure(_))

      case req @ GET -> Root / "status" / "overview"                         =>
        client.fetch(req.withUri(uri"http://status:8080/status/overview"))(Applicative[F].pure(_))
    }
  }
}
