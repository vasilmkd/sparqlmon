package mk.ukim.finki.wbs.sparqlmon.availability

import scala.concurrent.ExecutionContext

import java.net.URL
import java.time.Instant
import javax.mail.internet.InternetAddress

import cats.effect.IO
import fs2.kafka._
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.message._

class KafkaAvailabilityProducerSuite extends FunSuite {

  implicit private val contextShift = IO.contextShift(ExecutionContext.global)

  implicit private val timer = IO.timer(ExecutionContext.global)

  private val ps = ProducerSettings[IO, String, EndpointAvailability].withBootstrapServers("localhost:9092")

  private val ap = new KafkaAvailabilityProducer[IO](ps)

  private val cs = ConsumerSettings[IO, String, EndpointAvailability]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withEnableAutoCommit(true)
    .withBootstrapServers("localhost:9092")
    .withGroupId("testavailability")

  test("produceOne") {
    val expected =
      EndpointAvailability(
        Endpoint(new URL("http://dbpedia.org/sparql"), Some(new InternetAddress("someone@dbpedia.org"))),
        AvailabilityRecord(Instant.EPOCH, true)
      )
    val test     = for {
      _  <- ap.produceOne(expected)
      ea <- consumerStream(cs)
              .evalTap(_.subscribeTo("availability"))
              .flatMap(_.stream)
              .map(_.record.value)
              .take(1)
              .compile
              .lastOrError
    } yield assertEquals(ea, expected)
    test.unsafeRunSync()
  }
}
