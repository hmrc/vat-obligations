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

import base.SpecBase
import play.api.libs.json.{JsValue, Json}

class VatObligationsSpec extends SpecBase {

  val vatObligationsModel: VatObligations =
    VatObligations(
      Seq(VatObligation(
        ObligationIdentification("555555555", "VRN"),
        Seq(
          ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
          ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
        )
      )
      )
    )

  val vatObligationsJson: JsValue = Json.parse(
    """{
      |	"obligations": [{
      |		"identification": {
      |			"referenceNumber": "555555555",
      |			"referenceType": "VRN"
      |		},
      |		"obligationDetails": [{
      |				"status": "F",
      |				"inboundCorrespondenceFromDate": "1980-02-03",
      |				"inboundCorrespondenceToDate": "1980-04-05",
      |				"inboundCorrespondenceDateReceived": "1980-02-02",
      |				"inboundCorrespondenceDueDate": "1980-04-08",
      |				"periodKey": "17AA"
      |			},
      |			{
      |				"status": "F",
      |				"inboundCorrespondenceFromDate": "1981-02-03",
      |				"inboundCorrespondenceToDate": "1981-04-05",
      |				"inboundCorrespondenceDateReceived": "1981-02-02",
      |				"inboundCorrespondenceDueDate": "1981-04-08",
      |				"periodKey": "18AA"
      |			}
      |		]
      |	}]
      |}""".stripMargin)


  "VatObligations" should {

    "serialize to Json successfully" in {
      Json.toJson(vatObligationsModel) shouldBe vatObligationsJson
    }

    "deserialize to a Transaction model successfully" in {
      vatObligationsJson.as[VatObligations] shouldBe vatObligationsModel
    }

  }

}
