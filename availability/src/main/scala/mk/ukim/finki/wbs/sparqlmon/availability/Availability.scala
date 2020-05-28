package mk.ukim.finki.wbs.sparqlmon.availability

import java.net.URL

import mk.ukim.finki.wbs.sparqlmon.model.AvailabilityRecord

final case class Availability(url: URL, history: Vector[AvailabilityRecord])
