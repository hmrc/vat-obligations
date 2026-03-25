/*
 * Copyright 2023 HM Revenue & Customs
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
            Seq(
              ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
              ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
            )
          ))
        )

      val responseJson: JsValue = Json.parse(
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

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson.toString)

      val expected: Either[Nothing, VatObligations] = Right(testObligations)
      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a VatObligations instance" in {
        result shouldEqual expected
      }
    }

    "response status is 200 OK and matches expected Schema when the response contains multiple obligations with multiple details" should {

      val testObligations: VatObligations =
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

      val responseJson: JsValue = Json.parse(
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

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson.toString)

      val expected: Either[Nothing, VatObligations] = Right(testObligations)
      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a VatObligations instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, Json.obj("invalid" -> "data").toString)

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "code" -> "CODE",
          "reason" -> "ERROR MESSAGE"
        ).toString
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          reason = "ERROR MESSAGE"
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "code" -> "ERROR CODE 1",
              "reason" -> "ERROR MESSAGE 1"
            ),
            Json.obj(
              "code" -> "ERROR CODE 2",
              "reason" -> "ERROR MESSAGE 2"
            )
          )
        ).toString
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
          )
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, Json.obj("foo" -> "bar").toString)

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, "Banana")

      val expected =  Left(ErrorResponse(Status.BAD_REQUEST, Error("UNKNOWN_FORMAT", "Banana")))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 Internal Server Error" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        Json.obj(
          "code" -> "code",
          "reason" -> "message"
        ).toString
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          reason = "message"
        )
      ))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }

    "the http response status is unexpected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.SEE_OTHER, "")

      val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }

    "the http response status is 502 BAD_GATEWAY Html" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_GATEWAY,
        """
          |<html> <head><title>502 Bad Gateway</title></head> <body> <center>
          |<h1>502 Bad Gateway</h1></center> <hr><center>nginx/1.29.6</center> </body> </html>
          |""".stripMargin)

      val expected =  Left(ErrorResponse(Status.BAD_GATEWAY, Error("GATEWAY_ERROR", "Received HTML response from downstream")))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }


    "the http response status is Gateway error Html" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_GATEWAY,
        """
          |<html> <head><title>502 Bad Gateway</title></head> <body> <center>
          |<h1>502 Bad Gateway</h1></center> <hr><center>nginx/1.29.6</center> </body> </html>
          |""".stripMargin)

      val expected =  Left(ErrorResponse(Status.BAD_GATEWAY, Error("GATEWAY_ERROR", "Received HTML response from downstream")))

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response is an xml and not a json" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        """
          |<am:fault xmlns:am="http://wso2.org/apimanager"><am:code>101504</am:code><am:type>Status report</am:type>
          |<am:message>Runtime Error</am:message><am:description>Send timeout</am:description></am:fault>
          |""".stripMargin)

      val expected = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "TIMEOUT",
          reason = "Runtime Error - Send timeout"
        )
      ))

      val result = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND,"")

      val expected = Left(ErrorResponse(Status.NOT_FOUND, Error("EMPTY_RESPONSE","Downstream returned empty body")))

      val result = VatObligationsReads.read("", "", httpResponse)

      "return a NOT_FOUND status in an Error model" in {
        result shouldEqual expected
      }
    }
  }

}
