package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.{ MalformedURLException, URL }

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import mk.ukim.finki.wbs.sparqlmon.model._

class Service[F[_]: Sync: AvailabilityRepository] extends Http4sDsl[F] {

  object UrlParamDecoderMatcher extends QueryParamDecoderMatcher[String]("url")

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "availability" :? UrlParamDecoderMatcher(url) =>
      (for {
        ep      <- Sync[F].delay(Endpoint(new URL(url)))
        history <- AvailabilityRepository[F].availability(ep)
        res     <- Ok(Availability(ep.url, history).asJson)
      } yield res)
        .recoverWith {
          case _: MalformedURLException =>
            BadRequest(Error.MalformedUrlError(url).asJson)
        }
  }
}
