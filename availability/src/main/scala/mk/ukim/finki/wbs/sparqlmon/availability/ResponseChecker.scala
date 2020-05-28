package mk.ukim.finki.wbs.sparqlmon.availability

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import io.circe.Json
import org.http4s.{ EntityDecoder, Response, Status }

object ResponseChecker {

  def checkAskResponse[F[_]: Sync: EntityDecoder[*[_], Json]](res: Response[F]): OptionT[F, Unit] =
    if (res.status =!= Status.Ok) OptionT.none[F, Unit]
    else
      OptionT(
        res
          .as[Json]
          .map(_.hcursor.get[Boolean]("boolean").toOption.void)
          .handleError(_ => None)
      )

  def checkSelectResponse[F[_]: Sync: EntityDecoder[*[_], Json]](res: Response[F]): OptionT[F, Unit] =
    if (res.status =!= Status.Ok) OptionT.none[F, Unit]
    else
      OptionT(
        res
          .as[Json]
          .map(_.hcursor.downField("results").get[List[Json]]("bindings").toOption.filter(_.nonEmpty).void)
          .handleError(_ => None)
      )
}
