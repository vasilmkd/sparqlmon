package mk.ukim.finki.wbs.sparqlmon.registration

import cats.Applicative
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{ HttpRoutes, Request }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import mk.ukim.finki.wbs.sparqlmon.message._

class Service[F[_]: Sync: EndpointChecker: EndpointRepository: RegistrationProducer] extends Http4sDsl[F] {

  implicit private val decoder = jsonOf[F, Endpoint]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "endpoints"       =>
      for {
        es  <- EndpointRepository[F].endpoints
        res <- Ok(es.asJson)
      } yield res
    case req @ POST -> Root / "register" =>
      parseEndpoint(req)
        .flatTap { ep =>
          for {
            _ <- EitherT(EndpointChecker[F].check(ep))
            _ <- EitherT(EndpointRepository[F].register(ep))
          } yield ()
        }
        .redeemWith(
          e => EitherT.liftF(BadRequest(e.asJson)),
          ep =>
            for {
              _   <- EitherT.liftF(RegistrationProducer[F].produceOne(ep))
              res <- EitherT.liftF(Ok())
            } yield res
        )
        .getOrElseF(InternalServerError())
  }

  private def parseEndpoint(req: Request[F]): EitherT[F, Error, Endpoint] =
    EitherT {
      req
        .as[Endpoint]
        .redeemWith(
          _ => Applicative[F].pure(Left(Error.MalformedRegistrationRequest)),
          ep =>
            Sync[F]
              .delay(ep.email.validate())
              .as(ep)
              .redeem(
                _ => Error.MalformedRegistrationRequest.asLeft[Endpoint],
                Right(_)
              )
        )
    }
}
