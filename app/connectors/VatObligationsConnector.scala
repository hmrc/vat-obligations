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

package connectors

import javax.inject.{Inject, Singleton}
import config.MicroserviceAppConfig
import connectors.httpParsers.VatObligationsHttpParser._
import models.{VatObligationFilters, VatObligations}
import models.{Error, ErrorResponse}
import play.api.http.Status.NOT_FOUND
import play.api.http.Status.BAD_GATEWAY
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatObligationsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def setupDesVatObligationsUrl(vrn: String): String = appConfig.desServiceUrl +
    appConfig.setupDesObligationsStartPath + vrn + appConfig.setupDesObligationsEndPath

  val desHeaders = Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Environment" -> appConfig.desEnvironment)

  def getVatObligations(vrn: String, queryParameters: VatObligationFilters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatObligations]] = {

    val url = setupDesVatObligationsUrl(vrn)
    val hc = headerCarrier.copy(authorization = None)

    logger.debug(s"[VatObligationsConnector][getVatObligations] - Calling GET $url \nHeaders: $desHeaders\n QueryParams: $queryParameters")
    http.GET(url, queryParameters.toSeqQueryParams, desHeaders)(VatObligationsReads, hc, ec).map {
      case vatObligations@Right(_) =>
        vatObligations
      case error@Left(response) => response.status match {
        case NOT_FOUND =>
          logger.debug("[VatObligationsConnector][getVatObligations] Error Received: " + response)
          error
        case _ =>
          logger.warn("[VatObligationsConnector][getVatObligations] Error Received: " + response)
          error
      }
    }.recover {
      case ex: HttpException =>
        logger.warn(s"[VatObligationsConnector][getVatObligations] - HTTP exception received: ${ex.message}")
        Left(ErrorResponse(BAD_GATEWAY, Error("BAD_GATEWAY", ex.message)))
    }
  }
}
