/*
 * Copyright 2022 HM Revenue & Customs
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

import _root_.models.{ObligationDetail, VatObligation, VatObligations}
import base.SpecBase
import play.api.libs.json.{JsValue, Json}

class VatObligationsResponseAuditModelSpec extends SpecBase {

  val transactionName = "vat-obligations-response"
  val auditEvent = "vatObligationsResponse"
  val testVrn = "555555555"

  "The VatObligationsResponseAuditModel" when {

    "passed some transaction data in the response" should {

      val testTransactions: VatObligations =
        VatObligations(
          Seq(VatObligation(
            Seq(
              ObligationDetail("F", "1980-03-02", "1980-04-04", Some("1980-02-02"), "1980-05-02", "18AA"),
              ObligationDetail("F", "1981-04-02", "1981-05-04", Some("1981-03-02"), "1981-06-02", "19AA")
            )
          )
          )
        )

      object TestVatObligationsResponseAuditModel extends VatObligationsResponseAuditModel(testVrn, testTransactions)

      s"Have the correct transaction name of '$transactionName'" in {
        TestVatObligationsResponseAuditModel.transactionName shouldBe transactionName
      }

      s"Have the correct audit event type of '$auditEvent'" in {
        TestVatObligationsResponseAuditModel.auditType shouldBe auditEvent
      }


      "Have the correct details for the audit event" in {
        val expected: JsValue = Json.obj(
          "vrn" -> testVrn,
          "response" ->
            Json.toJson(testTransactions)
        )

        TestVatObligationsResponseAuditModel.detail shouldBe expected
      }
    }

    "not passed any transactions in the response" should {

      val noTransactions: VatObligations = VatObligations(Seq.empty[VatObligation])
      object TestVatObligationsResponseAuditModel extends VatObligationsResponseAuditModel(testVrn, noTransactions)

      "Have the correct details for the audit event" in {
        val expected: JsValue = Json.obj(
          "vrn" -> testVrn,
          "response" ->
            Json.toJson(noTransactions)
        )
        TestVatObligationsResponseAuditModel.detail shouldBe expected
      }
    }
  }
}
