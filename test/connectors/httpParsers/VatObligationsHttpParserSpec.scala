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
          Seq(
            VatObligation(
              Seq(
                ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
                ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
              )
            ))
        )

      val responseJson: JsValue = Json.parse("""{
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

      val httpResponse = HttpResponse(Status.OK, responseJson.toString)

      val expected: Either[Nothing, VatObligations]                      = Right(testObligations)
      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a VatObligations instance" in {
        result shouldEqual expected
      }
    }

    "response status is 200 OK and matches expected Schema when the response contains multiple obligations with multiple details" should {

      val testObligations: VatObligations =
        VatObligations(
          Seq(
            VatObligation(
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

      val responseJson: JsValue = Json.parse("""{
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

      val httpResponse = HttpResponse(Status.OK, responseJson.toString)

      val expected: Either[Nothing, VatObligations]                      = Right(testObligations)
      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return a VatObligations instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse = HttpResponse(Status.OK, Json.obj("invalid" -> "data").toString)

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response has a non-200 status" should {
      "return the error in a Left with the original status and handle the response body" when {
        "the response body matches the single Error format" in {
          val singleError  = Json.obj("code" -> "CODE", "reason" -> "ERROR MESSAGE").toString
          val httpResponse = HttpResponse(Status.BAD_REQUEST, singleError)

          val expectedError = ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE"))

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          result shouldEqual Left(expectedError)
        }

        "the response body matches the MultiError format" in {
          val multiError = Json
            .obj(
              "failures" -> Json.arr(
                Json.obj("code" -> "ERROR CODE 1", "reason" -> "ERROR MESSAGE 1"),
                Json.obj("code" -> "ERROR CODE 2", "reason" -> "ERROR MESSAGE 2")
              ))
            .toString
          val httpResponse = HttpResponse(Status.CONFLICT, multiError)

          val expectedError = ErrorResponse(
            Status.CONFLICT,
            MultiError(failures = Seq(
              Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
              Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
            )))

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          result shouldEqual Left(expectedError)
        }

        "the response body contains Json of an unexpected format, returning an 'UNEXPECTED_JSON_FORMAT' code" in {
          val unexpectedJsonFormat = Json.obj("notExpected" -> "what", "surprising" -> "huh").toString
          val httpResponse         = HttpResponse(Status.BAD_GATEWAY, unexpectedJsonFormat)

          val unexpectedJsonFormatError = ErrorResponse(Status.BAD_GATEWAY, Error(code = "UNEXPECTED_JSON_FORMAT", reason = unexpectedJsonFormat))

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          result shouldEqual Left(unexpectedJsonFormatError)
        }

        "the response body contains an HTML message, returning an 'HTML_RESPONSE' code and extracting the error message" in {
          val htmlFormat =
            "<html><head><title>Error</title></head><body><h1>502 Bad Gateway</h1></body></html>"
          val httpResponse = HttpResponse(Status.BAD_GATEWAY, htmlFormat)

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          val expectedHtmlError = ErrorResponse(Status.BAD_GATEWAY, Error(code = "HTML_RESPONSE", reason = "Error - 502 Bad Gateway"))
          result shouldEqual Left(expectedHtmlError)
        }

        "the response body contains an XML message, returning an 'XML_RESPONSE' code and extracting the error message" in {
          val xmlFormat = """
                                        |<am:fault xmlns:am="http://wso2.org/apimanager"><am:code>101504</am:code><am:type>Status report</am:type>
                                        |<am:message>Runtime Error</am:message><am:description>Send timeout</am:description></am:fault>
                                        |""".stripMargin
          val httpResponse = HttpResponse(Status.BAD_GATEWAY, xmlFormat)

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          val expectedXmlError = ErrorResponse(Status.BAD_GATEWAY, Error(code = "XML_RESPONSE", reason = "Runtime Error - Send timeout"))
          result shouldEqual Left(expectedXmlError)
        }

        "the response body contains non-Json, returning an 'INVALID_JSON' code" in {
          val unknownFormatBody = "This is not in a valid format"
          val httpResponse      = HttpResponse(Status.INTERNAL_SERVER_ERROR, unknownFormatBody)

          val unknownFormatError = ErrorResponse(Status.INTERNAL_SERVER_ERROR, Error(code = "INVALID_JSON", reason = unknownFormatBody))

          val result: VatObligationsHttpParser.HttpGetResult[VatObligations] = VatObligationsReads.read("", "", httpResponse)

          result shouldEqual Left(unknownFormatError)
        }
      }
    }
  }

}
