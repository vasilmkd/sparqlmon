package mk.ukim.finki.wbs.sparqlmon.registration

import java.net.URL
import javax.mail.internet.InternetAddress

import scala.concurrent.ExecutionContext

import cats.effect.IO
import fs2.kafka._
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

class KafkaRegistrationProducerSuite extends FunSuite {

  implicit private val contextShift = IO.contextShift(ExecutionContext.global)

  implicit private val timer = IO.timer(ExecutionContext.global)

  implicit private val ioEndpointRepository = new TestEndpointRepository[IO]

  private val ps = ProducerSettings[IO, String, Endpoint].withBootstrapServers("localhost:9092")

  private val rp = new KafkaRegistrationProducer[IO](ps)

  private val cs = ConsumerSettings[IO, String, Endpoint]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withEnableAutoCommit(true)
    .withBootstrapServers("localhost:9092")
    .withGroupId("testregistration")

  test("produceOne") {
    val expected = Endpoint(new URL("http://dbpedia.org/sparql"), new InternetAddress("someone@dbpedia.org"))
    val test     = for {
      _        <- rp.produceOne(expected)
      endpoint <- consumerStream(cs)
                    .evalTap(_.subscribeTo("registration"))
                    .flatMap(_.stream)
                    .map(_.record.value)
                    .take(1)
                    .compile
                    .lastOrError
    } yield assertEquals(endpoint, expected)
    test.unsafeRunSync()
  }

  test("produceRegistrations") {
    val expected = TestEndpointRepository.testEndpoints
    val test     = for {
      fiber <- rp.produceRegistrations.start
      eps   <- consumerStream(cs)
               .evalTap(_.subscribeTo("registration"))
               .flatMap(_.stream)
               .map(_.record.value)
               .take(2)
               .compile
               .fold(Set.empty[Endpoint])(_ + _)
      _     <- fiber.cancel
    } yield assertEquals(eps, expected)
    test.unsafeRunSync()
  }
}
