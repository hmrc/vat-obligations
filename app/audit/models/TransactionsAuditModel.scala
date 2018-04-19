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

package audit.models

import play.api.libs.json.{Format, Json}

/*TODO: I am not sure we need to audit any data we retrieve. Probabbly not as nothing useful?
 TODO: This code will need tweaking to meet any audit requirments from TXM, or removed if none */
case class TransactionsAuditModel(incomeSourceType: String,
                                  referenceNumber: String,
                                  referenceType: String)

object TransactionsAuditModel {
  implicit val format: Format[TransactionsAuditModel] = Json.format[TransactionsAuditModel]
}
