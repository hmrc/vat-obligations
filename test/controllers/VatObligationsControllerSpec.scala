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

package controllers

import base.SpecBase
import controllers.actions.AuthActionImpl
import mocks.auth.MockMicroserviceAuthorisedFunctions
import mocks.services.MockVatObligationsService
import models._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout, status}

import scala.concurrent.Future

class VatObligationsControllerSpec extends SpecBase with MockVatObligationsService with MockMicroserviceAuthorisedFunctions {

  val success: VatObligations =
    VatObligations(
      Seq(VatObligation(
        Seq(
          ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
          ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
        )
      )
      ))

  val successMultipleObligations: VatObligations =
    VatObligations(
      Seq(VatObligation(
        Seq(
          ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
          ObligationDetail("F", "1980-02-02", "1980-04-02", Some("1980-02-01"), "1980-04-07", "17AB"),
          ObligationDetail("F", "1981-02-03", "1981-04-05", None, "1981-05-08", "17AC")
        )
      ),
        VatObligation(
          Seq(
            ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "16AA"),
            ObligationDetail("F", "1981-02-02", "1981-04-02", Some("1981-02-01"), "1981-04-07", "16AB"),
            ObligationDetail("F", "1982-02-03", "1982-04-05", None, "1982-05-08", "16AC")
          )
        )
      )
    )

  val successEmptyDetail: VatObligations =
    VatObligations(
      Seq(VatObligation(
        Seq.empty[ObligationDetail]
      )
      ))

  val successEmptyObligations: VatObligations =
    VatObligations(
      Seq.empty[VatObligation]
    )

  val transformedSuccessData: Obligations = Obligations(Seq(
    Obligation("1981-02-03", "1981-04-05", "1981-04-08", "F", "18AA", Some("1981-02-02")),
    Obligation("1980-02-03", "1980-04-05", "1980-04-08", "F", "17AA", Some("1980-02-02"))
  ))

  val transformedMultipleObligationsSuccessData: Obligations = Obligations(Seq(
    Obligation("1982-02-03", "1982-04-05", "1982-05-08", "F", "16AC", None),
    Obligation("1981-02-03", "1981-04-05", "1981-05-08", "F", "17AC", None),
    Obligation("1981-02-03", "1981-04-05", "1981-04-08", "F", "16AA", Some("1981-02-02")),
    Obligation("1981-02-02", "1981-04-02", "1981-04-07", "F", "16AB", Some("1981-02-01")),
    Obligation("1980-02-03", "1980-04-05", "1980-04-08", "F", "17AA", Some("1980-02-02")),
    Obligation("1980-02-02", "1980-04-02", "1980-04-07", "F", "17AB", Some("1980-02-01"))
  ))

  val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2" +
        "", reason = "ERROR MESSAGE 2")
    )
  )

  val successResponse: Either[Nothing, VatObligations] = Right(success)
  val successResponseMultipleObligations: Either[Nothing, VatObligations] = Right(successMultipleObligations)
  val successResponseEmptyDetail: Either[Nothing, VatObligations] = Right(successEmptyDetail)
  val successResponseEmptyObligations: Either[Nothing, VatObligations] = Right(successEmptyObligations)
  val badRequestSingleError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, multiError))
  val testVrn: String = "555555555"
  val badVrn: String = "55"

  val authActionImpl = new AuthActionImpl(mockAuth, controllerComponents)

  "The GET VatObligationsController.VatObligations method" when {

    "called by an authenticated user" which {

      object TestVatObligationsController extends VatObligationsController(authActionImpl, mockVatObligationsService, controllerComponents)

      "is requesting VAT details" should {

        "for a successful response from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponse)
            status(Future.successful(result)) shouldBe Status.OK
          }

          "return a json body with the transformed des obligation data" in {
            contentAsJson(Future.successful(result)) shouldBe Json.toJson(transformedSuccessData)
          }
        }

        "for a successful response with multiple obligations from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponseMultipleObligations)
            status(Future.successful(result)) shouldBe Status.OK
          }

          "return a json body with the transformed des obligation data" in {
            contentAsJson(Future.successful(result)) shouldBe Json.toJson(transformedMultipleObligationsSuccessData)
          }
        }

        "for a bad request with single error from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestSingleError)
            status(Future.successful(result)) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            contentAsJson(Future.successful(result)) shouldBe Json.toJson(singleError)
          }
        }

        "for a result with an empty ObligationDetail sequence" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 404 (NotFound)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponseEmptyDetail)
            status(Future.successful(result)) shouldBe Status.NOT_FOUND
          }

        }

        "for a result with an empty VatObligation sequence" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 404 (NotFound)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(successResponseEmptyObligations)
            status(Future.successful(result)) shouldBe Status.NOT_FOUND
          }
        }

        "for an invalid vrn " should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(badVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            status(Future.successful(result)) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the invalid vrn error message" in {
            contentAsJson(Future.successful(result)) shouldBe Json.toJson(InvalidVrn)
          }
        }

        "for a bad request with multiple errors from the VatObligationsService" should {

          lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestMultiError)
            status(Future.successful(result)) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            contentAsJson(Future.successful(result)) shouldBe Json.toJson(multiError)
          }
        }

      }

      "for a bad request with single error from the VatObligationsService" should {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestSingleError)
          status(Future.successful(result)) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the single error message" in {

          contentAsJson(Future.successful(result)) shouldBe Json.toJson(singleError)
        }
      }

      "for a bad request with multiple errors from the VatObligationsService" should {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetVatObligations(testVrn, VatObligationFilters())(badRequestMultiError)
          status(Future.successful(result)) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the multiple error messages" in {

          contentAsJson(Future.successful(result)) shouldBe Json.toJson(multiError)
        }
      }
    }

    "called by an unauthenticated user" should {

      object TestVatObligationsController extends VatObligationsController(authActionImpl, mockVatObligationsService, controllerComponents)

      "Return an UNAUTHORISED response" which {

        lazy val result: Result = await(TestVatObligationsController.getVatObligations(testVrn, VatObligationFilters())(fakeRequest))

        "has status UNAUTHORISED (401)" in {
          setupMockAuthorisationException()
          status(Future.successful(result)) shouldBe Status.UNAUTHORIZED
        }
      }
    }
  }
}
