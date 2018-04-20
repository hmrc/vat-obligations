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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.VatObligationsHttpParser.VatObligationsReads
import models._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

class VatObligationsHttpParserSpec extends SpecBase {

  "The VatObligationsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" should {

      val testObligations: VatObligations =
        VatObligations(
          Seq(VatObligation(
            ObligationIdentification("ITSA", "555555555", "VRN"
            ),
            Seq(
              ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
              ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
            )
          )
          )
        )

      val responseJson: JsValue = Json.parse(
        """{
          |	"obligations": [{
          |		"identification": {
          |			"incomeSourceType": "ITSA",
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

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson = Some(
        responseJson
      ))

      val expected: Either[Nothing, VatObligations] = Right(testObligations)
      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a VatObligations instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson = Some(Json.obj("invalid" -> "data")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "CODE",
          "message" -> "ERROR MESSAGE"
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          message = "ERROR MESSAGE"
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "code" -> "ERROR CODE 1",
              "message" -> "ERROR MESSAGE 1"
            ),
            Json.obj(
              "code" -> "ERROR CODE 2",
              "message" -> "ERROR MESSAGE 2"
            )
          )
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", message = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", message = "ERROR MESSAGE 2")
          )
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Banana"))

      val expected: Either[InvalidJsonResponse.type, Nothing] = Left(InvalidJsonResponse)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 Internal Server Error" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        responseJson = Some(Json.obj(
          "code" -> "code",
          "message" -> "message"
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          message = "message"
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }

    "the http response status is unexpected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.SEE_OTHER)

      val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }

  }

}