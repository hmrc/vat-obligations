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

package audit.models

import _root_.models._
import base.SpecBase
import models.VatObligationFilters.{dateFromKey, dateToKey, statusKey}

class VatObligationsRequestAuditModelSpec extends SpecBase {

  val transactionName = "vat-obligations-request"
  val auditEvent = "vatObligationsRequest"
  val testVrn = "555555555"
  
  "The VatObligationsRequestAuditModel" when {

    "all QueryParameters are passed to it" should {

      val testQueryParams: VatObligationFilters = VatObligationFilters(
        from = Some(stringToDate("2018-01-01")),
        to = Some(stringToDate("2019-01-01")),
        status = Some("F")
      )
      object TestVatObligationsRequestAuditModel extends VatObligationsRequestAuditModel(testVrn, testQueryParams)

      s"Have the correct transaction name of '$transactionName'" in {
        TestVatObligationsRequestAuditModel.transactionName shouldBe transactionName
      }

      s"Have the correct audit event type of '$auditEvent'" in {
        TestVatObligationsRequestAuditModel.auditType shouldBe auditEvent
      }

      "Have the correct details for the audit event" in {
        TestVatObligationsRequestAuditModel.detail shouldBe Seq(
          "vrn" -> testVrn,
          dateFromKey -> testQueryParams.from.get.toString,
          dateToKey -> testQueryParams.to.get.toString,
          statusKey -> testQueryParams.status.get.toString
        )
      }
    }

    "not passed any Query Parameters" should {

      val noQueryParams: VatObligationFilters = VatObligationFilters()
      object TestVatObligationsRequestAuditModel extends VatObligationsRequestAuditModel(testVrn, noQueryParams)

      "Have the correct details for the audit event" in {
        TestVatObligationsRequestAuditModel.detail shouldBe Seq(
          "vrn" -> testVrn
        )
      }
    }
  }
}
