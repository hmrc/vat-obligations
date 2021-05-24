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

package connectors

import javax.inject.{Inject, Singleton}
import config.MicroserviceAppConfig
import connectors.httpParsers.VatObligationsHttpParser._
import models.{VatObligations, VatObligationFilters}
import play.api.http.Status.NOT_FOUND
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatObligationsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) {

  private[connectors] def setupDesVatObligationsUrl(vrn: String): String = appConfig.desServiceUrl +
    appConfig.setupDesObligationsStartPath + vrn + appConfig.setupDesObligationsEndPath

  def getVatObligations(vrn: String, queryParameters: VatObligationFilters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatObligations]] = {

    val url = setupDesVatObligationsUrl(vrn)
    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[VatObligationsConnector][getVatObligations] - Calling GET $url \nHeaders: $desHC\n QueryParams: $queryParameters")
    http.GET(url, queryParameters.toSeqQueryParams)(VatObligationsReads, desHC, ec).map {
      case vatObligations@Right(_) =>
        vatObligations
      case error@Left(response) => response.status match {
        case NOT_FOUND =>
          Logger.debug("[VatObligationsConnector][getVatObligations] Error Received: " + response)
          error
        case _ =>
          Logger.warn("[VatObligationsConnector][getVatObligations] Error Received: " + response)
          error
      }
    }
  }
}
