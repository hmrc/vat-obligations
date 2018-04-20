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

package connectors

import base.SpecBase
import connectors.httpParsers.VatObligationsHttpParser
import mocks.MockHttp
import models._
import play.api.http.Status
import utils.ImplicitDateFormatter._
import models.VatObligationFilters._

import scala.concurrent.Future

class VatObligationsConnectorSpec extends SpecBase with MockHttp {

  val testObligations: VatObligations =
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

  val successResponse: Either[Nothing, VatObligations] = Right(testObligations)
  val badRequestSingleError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", message = "ERROR MESSAGE")))
  val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", message = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", message = "ERROR MESSAGE 2")
    )
  )))

  val testVrn: String = "555555555"

  object TestVatObligationsConnector extends VatObligationsConnector(mockHttpGet, mockAppConfig)

  "The VatObligationsConnector" should {

    "format the request Url correctly for Income Tax TaxRegime requests" in {
      val actualUrl: String = TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn)
      val expectedUrl: String = s"${mockAppConfig.desServiceUrl}/obligation-data/555555555/obligations"
      actualUrl shouldBe expectedUrl
    }

    "when calling the getVatObligations" when {

      "calling for a user with all Query Parameters defined and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            dateFromKey -> "2018-04-06",
            dateToKey -> "2019-04-05",
            statusKey -> "F"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              from = Some("2018-04-06"),
              to = Some("2019-04-05"),
              status = Some("F")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with just the dateFrom Query Parameter and a success response is received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            dateFromKey -> "2017-04-06"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              from = Some("2017-04-06")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with dateTo Query Parameter and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            dateToKey -> "2018-04-05"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              to = Some("2018-04-05")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with just a status (F) Query Parameter and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            statusKey -> "F"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              status = Some("F")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with just a status (O) Query Parameter and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            statusKey -> "O"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              status = Some("O")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with dateFrom and dateTo Query Parameters and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            dateFromKey -> "2018-04-05",
            dateToKey -> "2018-04-18"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              from = Some("2018-04-05"),
              to = Some("2018-04-18")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with a status (O) and dateTo Query Parameters and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq(
            dateToKey -> "2018-04-05",
            statusKey -> "O"
          ))(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters(
              to = Some("2018-04-05"),
              status = Some("O")
            )
          )
          await(result) shouldBe successResponse
        }
      }

      "calling for a user with no Query Parameters defined and a success response received" should {

        "return a VatObligations model" in {
          setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq())(successResponse)
          val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
            vrn = testVrn,
            queryParameters = VatObligationFilters()
          )
          await(result) shouldBe successResponse
        }
      }
    }

    "calling for a user with non-success response received, single error" should {

      "return a Error model" in {
        setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq())(badRequestSingleError)
        val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
          vrn = testVrn,
          queryParameters = VatObligationFilters()
        )
        await(result) shouldBe badRequestSingleError
      }
    }

    "calling for a user with non-success response received, multi error" should {

      "return a MultiError model" in {
        setupMockHttpGet(TestVatObligationsConnector.setupDesVatObligationsUrl(testVrn), Seq())(badRequestMultiError)
        val result: Future[VatObligationsHttpParser.HttpGetResult[VatObligations]] = TestVatObligationsConnector.getVatObligations(
          vrn = testVrn,
          queryParameters = VatObligationFilters()
        )
        await(result) shouldBe badRequestMultiError
      }
    }

  }
}
