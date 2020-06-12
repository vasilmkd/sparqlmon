package mk.ukim.finki.wbs.sparqlmon.gateway

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
      case req @ GET -> Root / "api" / "endpoints"                                   =>
        client.toHttpApp.run(req.withUri(uri"http://registration:8080/endpoints"))

      case req @ POST -> Root / "api" / "register"                                   =>
        client.toHttpApp.run(req.withUri(uri"http://registration:8080/register"))

      case req @ GET -> Root / "api" / "availability" :? UrlParamDecoderMatcher(url) =>
        client.toHttpApp(req.withUri(uri"http://availability:8080/availability".withQueryParam("url", url)))

      case req @ GET -> Root / "api" / "status" :? UrlParamDecoderMatcher(url)       =>
        client.toHttpApp(req.withUri(uri"http://status:8080/status".withQueryParam("url", url)))

      case req @ GET -> Root / "api" / "status" / "overview"                         =>
        client.toHttpApp(req.withUri(uri"http://status:8080/status/overview"))
    }
  }
}
