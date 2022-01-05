/*
 * Copyright 2022 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import audit.AuditingService
import audit.models.{VatObligationsRequestAuditModel, VatObligationsResponseAuditModel}
import connectors.VatObligationsConnector
import models._
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatObligationsService @Inject()(val vatObligationsConnector: VatObligationsConnector, val auditingService: AuditingService)
  extends LoggerUtil {

  def getVatObligations(vrn: String,
                        queryParameters: VatObligationFilters)
                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, VatObligations]] = {

    logger.debug(s"[VatObligationsService][getVatObligations] Auditing Vat Obligations request")
    auditingService.audit(VatObligationsRequestAuditModel(vrn, queryParameters))

    logger.debug(s"[VatObligationsService][getVatObligations] Calling vatObligationsConnector with Vrn: $vrn\nParams: $queryParameters")
    vatObligationsConnector.getVatObligations(vrn, queryParameters).map {
      case success@Right(vatObligations) =>
        logger.debug(s"[VatObligationsService][getVatObligations] Auditing Vat Obligations response")
        auditingService.audit(VatObligationsResponseAuditModel(vrn, vatObligations))
        success
      case error@Left(_) =>
        error
    }
  }
}
