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

package testData

import models._

object ObligationData {

  val successResponse: VatObligations =
    VatObligations(
      Seq(VatObligation(
        ObligationIdentification("555555555", "VRN"
        ),
        Seq(
          ObligationDetail("F", "1980-02-03", "1980-04-05", Some("1980-02-02"), "1980-04-08", "17AA"),
          ObligationDetail("F", "1981-02-03", "1981-04-05", Some("1981-02-02"), "1981-04-08", "18AA")
        )
      )
      ))

  val transformedSuccessResponse: Obligations = Obligations(Seq(
    Obligation("1980-02-03", "1980-04-05", "1980-04-08", "F", "17AA", Some("1980-02-02")),
    Obligation("1981-02-03", "1981-04-05", "1981-04-08", "F", "18AA", Some("1981-02-02")))
  )

  val singleErrorResponse: Error = Error("CODE", "ERROR MESSAGE")

  val multiErrorModel: MultiError = MultiError(
    failures = Seq(
      Error("CODE 1", "ERROR MESSAGE 1"),
      Error("CODE 2", "ERROR MESSAGE 2")
    )
  )
}
