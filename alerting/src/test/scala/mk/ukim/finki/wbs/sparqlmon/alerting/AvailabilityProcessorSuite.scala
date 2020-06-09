package mk.ukim.finki.wbs.sparqlmon.alerting

import java.net.URL
import java.time.Instant
import javax.mail.internet.InternetAddress

import cats.effect.IO
import munit.FunSuite

import mk.ukim.finki.wbs.sparqlmon.message._

class AvailabilityProcessorSuite extends FunSuite {

  implicit private val ioAlerter = new TestAlerter[IO]

  private val endpoint = Endpoint(new URL("http://dbpedia.org"), Some(new InternetAddress("someone@dbpedia.org")))

  private val up = EndpointAvailability(
    endpoint,
    AvailabilityRecord(Instant.ofEpochMilli(1), true)
  )

  private val down = EndpointAvailability(
    endpoint,
    AvailabilityRecord(Instant.ofEpochMilli(2), false)
  )

  test("process endpoint availability empty and up") {
    val initial = AlertingState.empty
    val test    = AvailabilityProcessor.processEndpointAvailability[IO](up).runS(initial)
    assertEquals(test.unsafeRunSync(), Map(endpoint -> 0))
  }

  test("process endpoint availability empty and down") {
    val initial = AlertingState.empty
    val test    = AvailabilityProcessor.processEndpointAvailability[IO](down).runS(initial)
    assertEquals(test.unsafeRunSync(), Map(endpoint -> 1))
  }

  test("report when down 3 times before") {
    val initial = Map(endpoint -> 3)
    val test    = AvailabilityProcessor.processEndpointAvailability[IO](down).runS(initial)
    assertEquals(test.unsafeRunSync(), Map(endpoint -> 0))
  }

  test("reset when endpoint becomes available") {
    val initial = Map(endpoint -> 2)
    val test    = AvailabilityProcessor.processEndpointAvailability[IO](up).runS(initial)
    assertEquals(test.unsafeRunSync(), Map(endpoint -> 0))
  }
}
