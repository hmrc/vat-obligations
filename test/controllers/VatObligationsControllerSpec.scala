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

package controllers

import base.SpecBase
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockVatObligationsService
import models._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result

class VatObligationsControllerSpec extends SpecBase with MockVatObligationsService with MockMicroserviceAuthorisedFunctions {

  val success: VatObligations =
    VatObligations(
      Seq(VatObligation(
        ObligationIdentification("555555555", "VRN"),
        Seq(
          ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
          ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
        )
      )
      ))

  val successEmptyDetail: VatObligations =
    VatObligations(
      Seq(VatObligation(
        ObligationIdentification("555555555", "VRN"),
        Seq.empty[ObligationDetail]
      )
      ))

  val successEmptyObligations: VatObligations =
    VatObligations(
      Seq.empty[VatObligation]
    )

  val transformedSuccessData: Obligations = Obligations(Seq(
    Obligation("1980-02-03", "1980-04-05", "1980-04-08", "F", "17AA", Some("1980-02-02")),
    Obligation("1981-02-03", "1981-04-05", "1981-04-08", "F", "18AA", Some("1981-02-02")))
  )

  val singleError = Error(code = "CODE", message = "ERROR MESSAGE")
  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", message = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2" +
        "", message = "ERROR MESSAGE 2")
    )
  )

  val successResponse: Either[Nothing, VatObligations] = Right(success)
  val successResponseEmptyDetail: Either[Nothing, VatObligations] = Right(successEmptyDetail)
  val successResponseEmptyObligations: Either[Nothing, VatObligations] = Right(successEmptyObligations)
  val badRequestSingleError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, multiError))
  val testVrn: String = "555555555"
  val badVrn: String = "55"

  "The GET VatObligationsController.VatObligations method" when {

    "called by an authenticated user" which {

      object TestVatObligationsController extends VatObligationsController(new AuthActionImpl(mockAuth), mockVatObligationsService)

      "is requesting VAT details" should {

        "for a successful response from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the transformed des obligation data" in {
            jsonBodyOf(result) shouldBe Json.toJson(transformedSuccessData)
          }
        }

        "for a bad request with single error from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestSingleError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            jsonBodyOf(result) shouldBe Json.toJson(singleError)
          }
        }

        "for a result with an empty ObligationDetail sequence" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 404 (NotFound)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponseEmptyDetail)
            status(result) shouldBe Status.NOT_FOUND
          }

        }

        "for a result with an empty VatObligation sequence" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 404 (NotFound)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponseEmptyObligations)
            status(result) shouldBe Status.NOT_FOUND
          }
        }

        "for an invalid vrn " should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(badVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the invalid vrn error message" in {
            jsonBodyOf(result) shouldBe Json.toJson(InvalidVrn)
          }
        }

        "for a bad request with multiple errors from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            jsonBodyOf(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "for a bad request with single error from the VatObligationsService" should {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestSingleError)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the single error message" in {

          jsonBodyOf(result) shouldBe Json.toJson(singleError)
        }
      }

      "for a bad request with multiple errors from the VatObligationsService" should {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestMultiError)
          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the multiple error messages" in {

          jsonBodyOf(result) shouldBe Json.toJson(multiError)
        }
      }
    }

    "called by an unauthenticated user" should {

      object TestVatObligationsController extends VatObligationsController(new AuthActionImpl(mockAuth), mockVatObligationsService)

      "Return an UNAUTHORISED response" which {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(result) shouldBe Status.UNAUTHORIZED
        }
      }
    }
  }
}