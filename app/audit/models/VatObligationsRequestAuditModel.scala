/*
 * Copyright 2019 HM Revenue & Customs
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

package audit.models

import models._
import models.VatObligationFilters._

// TODO: This code will need tweaking to match audit requirements from TXM or otherwise, or removed if none.
case class VatObligationsRequestAuditModel(vrn: String, queryParams: VatObligationFilters) extends AuditModel {

  override val transactionName: String = "vat-obligations-request"
  override val auditType: String = "vatObligationsRequest"
  override val detail: Seq[(String, String)] = Seq(
    Some("vrn" -> vrn),
    queryParams.from.map(dateFromKey -> _.toString),
    queryParams.to.map(dateToKey -> _.toString),
    queryParams.status.map(statusKey -> _.toString)
  ).flatten
}
