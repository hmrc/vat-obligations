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

package services

import audit.models.{VatObligationsRequestAuditModel, VatObligationsResponseAuditModel}
import base.SpecBase
import mocks.audit.MockAuditingService
import mocks.connectors.MockVatObligationsConnector
import models._
import play.api.http.Status
import utils.ImplicitDateFormatter._

class VatObligationsServiceSpec extends SpecBase with MockVatObligationsConnector with MockAuditingService {

  object TestVatObligationService extends VatObligationsService(mockVatObligationsConnector, mockAuditingService)

  lazy val testVrn = "555555555"

  "The VatObligationService.getVatObligations method" should {

    "Return VatObligations when a success response is returned from the Connector" in {

      val vatObligations: VatObligations =
        VatObligations(
          Seq(VatObligation(
            ObligationIdentification("ITSA", "555555555", "VRN"
            ),
            Seq(
              ObligationDetail("F", "2017-04-10", "1980-04-11", Some("2017-04-10"), "2017-04-13", "17AA"),
              ObligationDetail("F", "2017-04-15", "1980-04-18", Some("2017-04-15"), "2017-04-18", "17AB")
            )
          )
          )
        )
      
      val successResponse: Either[Nothing, VatObligations] = Right(vatObligations)
      val queryParams: VatObligationFilters = VatObligationFilters(
        from = Some("2017-04-07"),
        to = Some("2018-04-06"),
        status = Some("O")
      )

      setupMockGetVatObligations(testVrn, queryParams)(successResponse)

      setupMockAuditEventResponse(VatObligationsResponseAuditModel(testVrn, vatObligations))
      setupMockAuditEventResponse(VatObligationsRequestAuditModel(testVrn, queryParams))

      val actual: Either[ErrorResponse, VatObligations] = await(TestVatObligationService.getVatObligations(
        testVrn,
        VatObligationFilters(
          from = Some("2017-04-07"),
          to = Some("2018-04-06"),
          status = Some("O")
        )
      ))

      actual shouldBe successResponse

      verifyAuditEvent(VatObligationsRequestAuditModel(testVrn, queryParams))
      verifyAuditEvent(VatObligationsResponseAuditModel(testVrn, vatObligations))
    }

    "Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE","MESSAGE")))

      setupMockGetVatObligations(testVrn, VatObligationFilters(
        from = Some("2017-04-06"),
        to = Some("2018-04-05"),
        status = Some("O")
      ))(singleErrorResponse)

      val actual: Either[ErrorResponse, VatObligations] = await(TestVatObligationService.getVatObligations(
        testVrn,
        VatObligationFilters(
          from = Some("2017-04-06"),
          to = Some("2018-04-05"),
          status = Some("O")
        )
      ))

      actual shouldBe singleErrorResponse
    }

    "Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1","MESSAGE 1"),
        Error("CODE 2","MESSAGE 2")
      ))))

      setupMockGetVatObligations(testVrn, VatObligationFilters(
        from = Some("2017-04-06"),
        to = Some("2018-04-05"),
        status = Some("O")
      ))(multiErrorResponse)

      val actual: Either[ErrorResponse, VatObligations] = await(TestVatObligationService.getVatObligations(
        testVrn,
        VatObligationFilters(
          from = Some("2017-04-06"),
          to = Some("2018-04-05"),
          status = Some("O")
        )
      ))

      actual shouldBe multiErrorResponse
    }

  }
}
