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

package connectors.httpParsers

import models._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.LoggerUtil

import scala.util.{Failure, Success, Try}


trait ResponseHttpParsers extends LoggerUtil {

  type HttpGetResult[T] = Either[ErrorResponse, T]

  private val MaxBodyLength = 300

  protected def handleErrorResponse(response: HttpResponse): Left[ErrorResponse, Nothing] = {

    val status = response.status
    val body = Option(response.body).getOrElse("").trim

    logger.debug(s"[ResponseHttpParsers] status=$status")

    val errorResponse: ErrorResponse = classifyBody(body) match {

      case Empty =>
        ErrorResponse(status, Error("EMPTY_RESPONSE", "Downstream returned empty body"))

      case JsonBody =>
        parseJson(body, status)

      case XmlBody =>
        val message = extractXmlMessage(body)
        val code = if (message.toLowerCase.contains("timeout")) "TIMEOUT" else "BACKEND_FAULT"
        logger.info(s"[ResponseHttpParsers] XML response received: $message")
        ErrorResponse(status, Error(code, message))

      case HtmlBody =>
        logger.info("[ResponseHttpParsers] HTML response received from downstream")
        ErrorResponse(
          status,
          Error("GATEWAY_ERROR", "Received HTML response from downstream")
        )

      case Unknown =>
        val truncated = body.take(MaxBodyLength)
        logger.warn(s"[ResponseHttpParsers] Unknown response format: $truncated")
        ErrorResponse(
          status,
          Error("UNKNOWN_FORMAT", truncated)
        )
    }

    Left(errorResponse)
  }

  private def classifyBody(body: String): BodyType = body match {
    case "" => Empty
    case b if b.startsWith("{") || b.startsWith("[") => JsonBody
    case b if b.contains("<am:fault") => XmlBody
    case b if b.toLowerCase.contains("<html") => HtmlBody
    case _ => Unknown
  }

  private def parseJson(body: String, status: Int): ErrorResponse =
    Try(Json.parse(body)) match {

      case Success(json) =>
        json.asOpt[MultiError]
          .orElse(json.asOpt[Error]) match {

          case Some(err) =>
            ErrorResponse(status, err)

          case None =>
            val truncated = body.take(MaxBodyLength)
            logger.warn(s"[ResponseHttpParsers] Unexpected JSON structure: $truncated")
            UnexpectedJsonFormat
        }

      case Failure(_) =>
        val truncated = body.take(MaxBodyLength)
        logger.info(s"[ResponseHttpParsers] Invalid JSON response: $truncated")
        InvalidJsonResponse
    }

  private def extractXmlMessage(xml: String): String = {

    def extract(tag: String) =
      s"<$tag>(.*?)</$tag>".r.findFirstMatchIn(xml).map(_.group(1))

    List(
      extract("am:message"),
      extract("am:description")
    ).flatten.mkString(" - ") match {
      case "" => "XML fault received"
      case m => m
    }
  }

  private sealed trait BodyType

  private case object Empty extends BodyType

  private case object JsonBody extends BodyType

  private case object XmlBody extends BodyType

  private case object HtmlBody extends BodyType

  private case object Unknown extends BodyType
}
