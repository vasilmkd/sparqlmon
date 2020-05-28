package mk.ukim.finki.wbs.sparqlmon.model

import java.time.Instant

final case class AvailabilityRecord(instant: Instant, up: Boolean)
