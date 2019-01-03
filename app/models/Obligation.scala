/*
 * Copyright 2019 HM Revenue & Customs
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

package models

import play.api.libs.json.{Json, OFormat}


case class Obligations(obligations: Seq[Obligation])

object Obligations {
  implicit val format: OFormat[Obligations] = Json.format[Obligations]
}

case class Obligation(start: String, end: String, due: String, status: String,
                      periodKey : String, received : Option[String] = None)

object Obligation {
  implicit val format: OFormat[Obligation] = Json.format[Obligation]
}
