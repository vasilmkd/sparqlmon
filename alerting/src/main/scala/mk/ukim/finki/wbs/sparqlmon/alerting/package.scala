package mk.ukim.finki.wbs.sparqlmon

import mk.ukim.finki.wbs.sparqlmon.model.Endpoint

package object alerting {
  type AlertingState = Map[Endpoint, Int]
}
