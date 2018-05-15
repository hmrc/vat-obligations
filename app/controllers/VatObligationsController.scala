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

import javax.inject.{Inject, Singleton}
import controllers.actions.AuthAction
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.VatObligationsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class VatObligationsController @Inject()(val authenticate: AuthAction,
                                                val vatObligationsService: VatObligationsService) extends BaseController {

  def getVatObligations(vrn: String, filters: VatObligationFilters): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        if (isInvalidVrn(vrn)) {
          Logger.warn(s"[VatObligationsController][getVatObligations] Invalid Vrn '$vrn' received in request.")
          Future.successful(BadRequest(Json.toJson(InvalidVrn)))
        } else {
          retrieveVatObligations(vrn, filters)
        }
    }

  private def retrieveVatObligations(vrn: String, filters: VatObligationFilters)(implicit hc: HeaderCarrier) = {
    Logger.debug(s"[VatObligationsController][retrieveVatObligations] Calling VatObligationsService.getVatObligations")
    vatObligationsService.getVatObligations(vrn, filters).map {
      case _@Right(vatObligations) =>
        val transformed = desTransform(vatObligations, vrn)
        if (transformed.obligations.isEmpty) NotFound else Ok(Json.toJson(transformed))
      case _@Left(error) => error.error match {
        case singleError: Error => Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  private def desTransform(vatObligations: VatObligations, vrn: String): Obligations = {
    vatObligations.obligations.find(obj => obj.identification.referenceNumber == vrn
      && obj.identification.referenceType == "VRN").fold(Obligations(Seq.empty[Obligation])) { obligations =>
      val found = obligations.obligationDetails.map { obligation =>
        Obligation(obligation.inboundCorrespondenceFromDate,
          obligation.inboundCorrespondenceToDate,
          obligation.inboundCorrespondenceDueDate,
          obligation.status,
          obligation.periodKey, obligation.inboundCorrespondenceDateReceived)
      }
      Obligations(found)
    }
  }

  private def isInvalidVrn(vrn: String): Boolean = {
    val vrnRegex = """^\d{9}$"""
    !vrn.matches(vrnRegex)
  }
}
