package mk.ukim.finki.wbs.sparqlmon

import mk.ukim.finki.wbs.sparqlmon.message.Endpoint

package object alerting {
  type AlertingState = Map[Endpoint, Int]
}
