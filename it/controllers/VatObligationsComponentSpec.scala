/*
 * Copyright 2021 HM Revenue & Customs
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
import helpers.servicemocks.{AuthStub, DesVatObligationsStub}
import models._
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import testData.ObligationData

class VatObligationsComponentSpec extends ComponentSpecBase {

  lazy val vrn: String = "555555555"

  def getStubResponse(responseStatus: Integer, responseJson: JsValue, authorised: Boolean = true): WSResponse = {
    if(authorised) AuthStub.stubAuthorised() else AuthStub.stubUnauthorised()
    val queryParameters: VatObligationFilters = VatObligationFilters()
    DesVatObligationsStub.stubGetVatObligations(vrn, queryParameters)(responseStatus, responseJson)
    val response = VatObligationsComponent.getVatObligations(vrn, queryParameters)
    DesVatObligationsStub.verifyGetVatObligations(vrn, queryParameters)
    response
  }

  "Sending a request to /vat-obligations/:vrn/obligations  (VatObligationsController)" when {

    "a user is authorised with a valid request with no query parameters and a success response" should {

      "return a status of 200 (OK)" in {
        val response: WSResponse = getStubResponse(OK, ObligationData.successResponse)
        response.status shouldBe 200
      }

      "return the expected success response" in {
        val response: WSResponse = getStubResponse(OK, Json.toJson(ObligationData.successResponse))
        response.json.as[Obligations] shouldBe ObligationData.transformedSuccessResponse
      }
    }

    "a user is authorised with a valid request with no query parameters and an error response" should {

      "return a status of 400 (BAD REQUEST)" in {
        val response: WSResponse = getStubResponse(BAD_REQUEST, Json.toJson(ObligationData.singleErrorResponse))
        response.status shouldBe BAD_REQUEST
      }

      "return a single error response" in {
        val response: WSResponse = getStubResponse(BAD_REQUEST, Json.toJson(ObligationData.singleErrorResponse))
        response.json.as[Error] shouldBe ObligationData.singleErrorResponse
      }
    }

    "a user is authorised with a valid request with no query parameters and a multi error response" should {

      "return a status of 400 (BAD REQUEST)" in {
        val response: WSResponse = getStubResponse(BAD_REQUEST, Json.toJson(ObligationData.multiErrorModel))
        response.status shouldBe BAD_REQUEST
      }

      "return a multi error response model" in {
        val response: WSResponse = getStubResponse(BAD_REQUEST, Json.toJson(ObligationData.multiErrorModel))
        response.json.as[MultiError] shouldBe ObligationData.multiErrorModel
      }
    }

    "a user is unauthorised" should {

      "return a FORBIDDEN response" in {
        AuthStub.stubUnauthorised()
        val response: WSResponse = VatObligationsComponent.getVatObligations(vrn, VatObligationFilters())
        response.status shouldBe FORBIDDEN
      }
    }
  }
}
