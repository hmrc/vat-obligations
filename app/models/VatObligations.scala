/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class VatObligations(obligations: Seq[VatObligation])

object VatObligations {
  implicit val format: OFormat[VatObligations] = Json.format[VatObligations]
}

case class VatObligation(obligationDetails: Seq[ObligationDetail])

object VatObligation {
  implicit val format: OFormat[VatObligation] = Json.format[VatObligation]
}

case class ObligationDetail(status: String,
                            inboundCorrespondenceFromDate: String,
                            inboundCorrespondenceToDate: String,
                            inboundCorrespondenceDateReceived: Option[String],
                            inboundCorrespondenceDueDate: String,
                            periodKey: String)

object ObligationDetail {

  implicit val reads: Reads[ObligationDetail] = Json.reads[ObligationDetail]

  implicit val clientDataWriter: Writes[ObligationDetail] = (
    (__ \ "status").write[String] and
      (__ \ "start").write[String] and
      (__ \ "end").write[String] and
      (__ \ "received").writeNullable[String] and
      (__ \ "due").write[String] and
      (__ \ "periodKey").write[String]
    ) (unlift(ObligationDetail.unapply))
}
