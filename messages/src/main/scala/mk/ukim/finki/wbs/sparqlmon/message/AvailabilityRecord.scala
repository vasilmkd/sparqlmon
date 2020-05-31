package mk.ukim.finki.wbs.sparqlmon.message

import java.time.Instant

final case class AvailabilityRecord(instant: Instant, up: Boolean)
