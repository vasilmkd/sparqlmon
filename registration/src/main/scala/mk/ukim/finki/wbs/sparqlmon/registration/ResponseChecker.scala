package mk.ukim.finki.wbs.sparqlmon.registration

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.Json
import org.http4s.{ EntityDecoder, Response, Status }

object ResponseChecker {
  def checkResponse[F[_]: Sync: EntityDecoder[*[_], Json]](res: Response[F]): F[Option[Unit]] =
    if (res.status =!= Status.Ok) Applicative[F].pure(None)
    else
      res
        .as[Json]
        .map(_.hcursor.downField("results").get[List[Json]]("bindings").toOption.filter(_.nonEmpty).void)
        .handleError(_ => None)
}
