package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL

import mk.ukim.finki.wbs.sparqlmon.message.AvailabilityRecord

final case class Availability(url: URL, history: Vector[AvailabilityRecord])
