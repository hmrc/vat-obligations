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

  protected def handleErrorResponse(response: HttpResponse): ErrorResponse = {
    val responseBody = Option(response.body).getOrElse("").trim
    val error = if (responseBody == "") {
      Error("EMPTY_RESPONSE", "Downstream returned empty body")
    } else if (responseBody.contains("<am:fault")) {
      val message = extractXmlMessage(responseBody)
      Error("XML_RESPONSE", message)
    } else if (responseBody.contains("<html")) {
      val message = extractHtmlMessage(responseBody)
      Error("HTML_RESPONSE", message)
    } else {
      attemptToParseErrorToJson(responseBody)
    }

    ErrorResponse(response.status, error)
  }

  private def attemptToParseErrorToJson(body: String): Errors =
    Try(Json.parse(body)) match {
      case Success(json) =>
        json.asOpt[MultiError].orElse(json.asOpt[Error]) match {
          case Some(error) =>
            error
          case None =>
            val truncatedErrorMsg = body.take(MaxBodyLength)
            Error(code = "UNEXPECTED_JSON_FORMAT", reason = truncatedErrorMsg)
        }
      case Failure(_) =>
        val truncatedErrorMsg = body.take(MaxBodyLength)
        Error(code = "INVALID_JSON", reason = truncatedErrorMsg)
    }

  private def extractXmlMessage(xml: String): String =
    List(
      extractContentBetweenTags("am:message", xml),
      extractContentBetweenTags("am:description", xml)
    ).flatten.mkString(" - ") match {
      case "" => s"Unable to extract message: $xml"
      case m  => m
    }

  private def extractHtmlMessage(html: String): String =
    List(
      extractContentBetweenTags("title", html),
      extractContentBetweenTags("h1", html)
    ).flatten.mkString(" - ") match {
      case "" => s"Unable to extract message: $html"
      case m  => m
    }

  private def extractContentBetweenTags(tag: String, content: String): Option[String] =
    s"<$tag>(.*?)</$tag>".r.findFirstMatchIn(content).map(_.group(1))

}
