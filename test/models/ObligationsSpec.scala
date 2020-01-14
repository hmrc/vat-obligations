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

import base.SpecBase
import play.api.libs.json.{JsValue, Json}

class ObligationsSpec extends SpecBase {

  val obligationsModel: Obligations =
    Obligations(
      Seq(
        Obligation("1980-02-03", "1980-04-05", "1980-04-08", "F", "17AA", Some("1980-02-02")),
        Obligation("1981-02-03", "1981-04-05", "1981-04-08", "F", "18AA", Some("1981-02-02"))
      )
    )

  val obligationsJson: JsValue = Json.parse(
    """{
      |    "obligations": [
      |      {
      |        "start": "1980-02-03",
      |        "end": "1980-04-05",
      |        "due": "1980-04-08",
      |        "status": "F",
      |        "received": "1980-02-02",
      |        "periodKey": "17AA"
      |      },
      |      {
      |        "start": "1981-02-03",
      |        "end": "1981-04-05",
      |        "due": "1981-04-08",
      |        "status": "F",
      |        "received": "1981-02-02",
      |        "periodKey": "18AA"
      |      }
      |    ]
      |  }""".stripMargin)


  "Obligations" should {

    "serialize to Json successfully" in {
      Json.toJson(obligationsModel) shouldBe obligationsJson
    }

    "deserialize to a Transaction model successfully" in {
      obligationsJson.as[Obligations] shouldBe obligationsModel
    }
  }
}
