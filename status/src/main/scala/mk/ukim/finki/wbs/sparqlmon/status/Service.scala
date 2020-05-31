package mk.ukim.finki.wbs.sparqlmon.status

import java.net.{ MalformedURLException, URL }

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import mk.ukim.finki.wbs.sparqlmon.message._

class Service[F[_]: Sync: StatusRepository] extends Http4sDsl[F] {

  object UrlParamDecoderMatcher extends QueryParamDecoderMatcher[String]("url")

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "status" :? UrlParamDecoderMatcher(url) =>
      (for {
        ep     <- Sync[F].delay(new URL(url))
        status <- StatusRepository[F].status(ep)
        res    <- Ok(Status(ep, status).asJson)
      } yield res)
        .recoverWith {
          case _: MalformedURLException =>
            BadRequest(Error.MalformedUrlError(url).asJson)
        }

    case GET -> Root / "status" / "overview"                   =>
      for {
        overview <- StatusRepository[F].overview
        res      <- Ok(overview.asJson)
      } yield res
  }
}
