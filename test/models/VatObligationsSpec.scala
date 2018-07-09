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


  "VatObligations when there is a single obligation" should {

    val singleObligationInputJson: JsValue = Json.parse(
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

    val singleObligationOutputJson: JsValue = Json.parse(
      """{
        |	"obligations": [{
        |		"obligationDetails": [{
        |			"status": "F",
        |			"start": "1980-02-03",
        |			"end": "1980-04-05",
        |			"received": "1980-02-02",
        |			"due": "1980-04-08",
        |			"periodKey": "17AA"
        |		}, {
        |			"status": "F",
        |			"start": "1981-02-03",
        |			"end": "1981-04-05",
        |			"received": "1981-02-02",
        |			"due": "1981-04-08",
        |			"periodKey": "18AA"
        |		}]
        |	}]
        |}""".stripMargin)

    val singleObligationsModel: VatObligations =
      VatObligations(
        Seq(VatObligation(
          Seq(
            ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
            ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
          )
        )
        )
      )

    "serialize to Json successfully" in {
      Json.toJson(singleObligationsModel) shouldBe singleObligationOutputJson
    }

    "deserialize to a Transaction model successfully" in {
      singleObligationInputJson.as[VatObligations] shouldBe singleObligationsModel
    }

  }


  "VatObligations with multiple obligations" should {

    val multipleObligationInputJson: JsValue = Json.parse(
      """{
        |	"obligations": [{
        |		"obligationDetails": [{
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1980-02-03",
        |			"inboundCorrespondenceToDate": "1980-04-05",
        |			"inboundCorrespondenceDateReceived": "1980-02-02",
        |			"inboundCorrespondenceDueDate": "1980-04-08",
        |			"periodKey": "17AA"
        |		}, {
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1980-02-02",
        |			"inboundCorrespondenceToDate": "1980-04-02",
        |			"inboundCorrespondenceDateReceived": "1980-02-01",
        |			"inboundCorrespondenceDueDate": "1980-04-07",
        |			"periodKey": "17AB"
        |		}, {
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1981-02-03",
        |			"inboundCorrespondenceToDate": "1981-04-05",
        |			"inboundCorrespondenceDueDate": "1981-04-08",
        |			"periodKey": "17AC"
        |		}]
        |	}, {
        |		"obligationDetails": [{
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1981-02-03",
        |			"inboundCorrespondenceToDate": "1981-04-05",
        |			"inboundCorrespondenceDateReceived": "1981-02-02",
        |			"inboundCorrespondenceDueDate": "1981-04-08",
        |			"periodKey": "16AA"
        |		}, {
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1981-02-02",
        |			"inboundCorrespondenceToDate": "1981-04-02",
        |			"inboundCorrespondenceDateReceived": "1981-02-01",
        |			"inboundCorrespondenceDueDate": "1981-04-07",
        |			"periodKey": "16AB"
        |		}, {
        |			"status": "F",
        |			"inboundCorrespondenceFromDate": "1982-02-03",
        |			"inboundCorrespondenceToDate": "1982-04-05",
        |			"inboundCorrespondenceDueDate": "19821-04-08",
        |			"periodKey": "16AC"
        |		}]
        |	}]
        |}""".stripMargin)

    val multipleObligationOutputJson: JsValue = Json.parse(
      """{
        |	"obligations": [{
        |		"obligationDetails": [{
        |			"status": "F",
        |			"start": "1980-02-03",
        |			"end": "1980-04-05",
        |			"received": "1980-02-02",
        |			"due": "1980-04-08",
        |			"periodKey": "17AA"
        |		}, {
        |			"status": "F",
        |			"start": "1980-02-02",
        |			"end": "1980-04-02",
        |			"received": "1980-02-01",
        |			"due": "1980-04-07",
        |			"periodKey": "17AB"
        |		}, {
        |			"status": "F",
        |			"start": "1981-02-03",
        |			"end": "1981-04-05",
        |			"due": "1981-04-08",
        |			"periodKey": "17AC"
        |		}]
        |	}, {
        |		"obligationDetails": [{
        |			"status": "F",
        |			"start": "1981-02-03",
        |			"end": "1981-04-05",
        |			"received": "1981-02-02",
        |			"due": "1981-04-08",
        |			"periodKey": "16AA"
        |		}, {
        |			"status": "F",
        |			"start": "1981-02-02",
        |			"end": "1981-04-02",
        |			"received": "1981-02-01",
        |			"due": "1981-04-07",
        |			"periodKey": "16AB"
        |		}, {
        |			"status": "F",
        |			"start": "1982-02-03",
        |			"end": "1982-04-05",
        |			"due": "19821-04-08",
        |			"periodKey": "16AC"
        |		}]
        |	}]
        |}""".stripMargin)


    val multipleObligationsModel: VatObligations =
      VatObligations(
        Seq(VatObligation(
          Seq(
            ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
            ObligationDetail("F", "1980-02-02", "1980-04-02", Some("1980-02-01"), "1980-04-07", "17AB"),
            ObligationDetail("F", "1981-02-03", "1981-04-05", None, "1981-04-08", "17AC")
          )
        ),
          VatObligation(
            Seq(
              ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "16AA"),
              ObligationDetail("F", "1981-02-02", "1981-04-02", Some("1981-02-01"), "1981-04-07", "16AB"),
              ObligationDetail("F", "1982-02-03", "1982-04-05", None, "19821-04-08", "16AC")
            )
          )
        )
      )

    "serialize toJson successfully flattening the output to match that expected by frontend when there multiple obligations" +
      "with multiple details" in {
      Json.toJson(multipleObligationsModel) shouldBe multipleObligationOutputJson
    }

    "deserialize to a Transaction model successfully" in {
      multipleObligationInputJson.as[VatObligations] shouldBe multipleObligationsModel
    }
  }

}

