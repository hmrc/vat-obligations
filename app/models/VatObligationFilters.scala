/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import java.time.LocalDate
import play.api.libs.json.{Format, Json}

case class VatObligationFilters(from: Option[LocalDate] = None,
                                to: Option[LocalDate] = None,
                                status: Option[String] = None) {
  import VatObligationFilters._
  val toSeqQueryParams: Seq[(String, String)] = Seq(
    from.map(dateFromKey -> _.toString),
    to.map(dateToKey -> _.toString),
    status.map(statusKey -> _.toString)
  ).flatten
  val hasQueryParameters: Boolean = toSeqQueryParams.nonEmpty
}

object VatObligationFilters {

  val dateFromKey: String = "from"
  val dateToKey: String = "to"
  val statusKey: String = "status"
  implicit val format: Format[VatObligationFilters] = Json.format[VatObligationFilters]
}
