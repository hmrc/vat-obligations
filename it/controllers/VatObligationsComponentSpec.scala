/*
 * Copyright 2017 HM Revenue & Customs
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

import helpers.ComponentSpecBase
import helpers.servicemocks.DesVatObligationsStub
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import testData.ObligationData

class VatObligationsComponentSpec extends ComponentSpecBase {

  "Sending a request to /vat-obligations/:vrn/obligations  (VatObligationsController)" when {

    "Requesting Vat Obligations" should {

      lazy val vrn: String = "555555555"

      "be authorised with a valid request with no query parameters and a success response" should {

        lazy val queryParameters: VatObligationFilters = VatObligationFilters()

        "return a success response" in {

          isAuthorised()

          And("When wiremock stubbing a successful Get Vat Obligations Data response")
          DesVatObligationsStub.stubGetVatObligations(vrn, queryParameters)(OK,
            Json.toJson(ObligationData.successResponse))

          When(s"Calling GET /vat-obligations/$vrn/obligations")
          val res: WSResponse = VatObligationsComponent.getVatObligations("555555555", queryParameters)

          DesVatObligationsStub.verifyGetVatObligations(vrn, queryParameters)

          Then("a successful response is returned with the correct transformed vat obligations")
          res should have(
            httpStatus(OK),
            jsonBodyAs[Obligations](ObligationData.transformedSuccessResponse)
          )
        }
      }

      "authorised with a valid request with no query parameters and an error response" should {

        lazy val queryParameters: VatObligationFilters = VatObligationFilters()

        "return a single error response" in {

          isAuthorised()

          And("When wiremock stubbing a successful Get Vat Obligations Data response")
          DesVatObligationsStub.stubGetVatObligations(vrn, queryParameters)(BAD_REQUEST,
            Json.toJson(ObligationData.singleErrorResponse))

          When(s"Calling GET /vat-obligations/$vrn/obligations")
          val res: WSResponse = VatObligationsComponent.getVatObligations(vrn, queryParameters)

          DesVatObligationsStub.verifyGetVatObligations(vrn, queryParameters)

          Then("the correct single error response is returned")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[Error](ObligationData.singleErrorResponse)
          )
        }
      }

      "authorised with a valid request with no query parameters and a multi error response" should {

        lazy val queryParameters: VatObligationFilters = VatObligationFilters()

        "return a multi error response model" in {

          isAuthorised()

          And("When wiremock stubbing a successful Get Vat Obligations Data response")
          DesVatObligationsStub.stubGetVatObligations(vrn, queryParameters)(BAD_REQUEST,
            Json.toJson(ObligationData.multiErrorModel))

          When(s"Call GET /vat-obligations/$vrn/obligations")
          val res: WSResponse = VatObligationsComponent.getVatObligations("555555555", queryParameters)

          DesVatObligationsStub.verifyGetVatObligations(vrn, queryParameters)

          Then("the correct multi error response is returned")

          res should have(
            httpStatus(BAD_REQUEST),
            jsonBodyAs[MultiError](ObligationData.multiErrorModel)
          )
        }
      }

      "unauthorised" should {

        "return an FORBIDDEN response" in {

          isAuthorised(false)

          When(s"Calling GET /vat-obligations/$vrn/obligations")
          val res: WSResponse = VatObligationsComponent.getVatObligations(vrn, VatObligationFilters())

          res should have(
            httpStatus(FORBIDDEN)
          )
        }
      }
    }

  }
}
