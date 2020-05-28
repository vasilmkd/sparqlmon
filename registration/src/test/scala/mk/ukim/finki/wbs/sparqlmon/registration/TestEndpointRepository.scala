package mk.ukim.finki.wbs.sparqlmon.registration

import java.net.URL

import cats.Applicative
import fs2.Stream

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class TestEndpointRepository[F[_]: Applicative] extends EndpointRepository[F] {
  override def endpointsStream: Stream[F, Endpoint] =
    Stream.emits(TestEndpointRepository.testEndpoints.toList)

  override def endpoints: F[Set[Endpoint]] =
    Applicative[F].pure(TestEndpointRepository.testEndpoints)

  override def register(ep: Endpoint): F[Either[Error, Unit]] =
    Applicative[F].pure(Right(()))
}

object TestEndpointRepository {
  val testEndpoint: Endpoint =
    Endpoint(new URL("http://dbpedia.org/sparql"))

  val testEndpoints: Set[Endpoint] =
    Set(
      testEndpoint,
      Endpoint(new URL("https://query.wikidata.org/sparql"))
    )
}
