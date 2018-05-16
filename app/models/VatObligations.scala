/*
 * Copyright 2018 HM Revenue & Customs
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

case class VatObligations(obligations: Seq[VatObligation])

object VatObligations {
  implicit val format: OFormat[VatObligations] = Json.format[VatObligations]
}

case class VatObligation(identification: ObligationIdentification, obligationDetails: Seq[ObligationDetail])

object VatObligation {
  implicit val format: OFormat[VatObligation] = Json.format[VatObligation]
}

case class ObligationIdentification(referenceNumber: String, referenceType: String)

object ObligationIdentification {
  implicit val format: OFormat[ObligationIdentification] = Json.format[ObligationIdentification]
}

case class ObligationDetail(status: String,
                            inboundCorrespondenceFromDate: String,
                            inboundCorrespondenceToDate: String,
                            inboundCorrespondenceDateReceived: Option[String],
                            inboundCorrespondenceDueDate: String,
                            periodKey: String)

object ObligationDetail {
  implicit val format: OFormat[ObligationDetail] = Json.format[ObligationDetail]
}
