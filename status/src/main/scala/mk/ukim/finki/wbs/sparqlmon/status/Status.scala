package mk.ukim.finki.wbs.sparqlmon.status

import java.net.URL

import mk.ukim.finki.wbs.sparqlmon.message.AvailabilityRecord

final case class Status(url: URL, status: Option[AvailabilityRecord])
