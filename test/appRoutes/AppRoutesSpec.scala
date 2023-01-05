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

package appRoutes

import base.SpecBase
import models.VatObligationFilters

class AppRoutesSpec extends SpecBase {

  "The reverse route for VatObligationsController.getVatObligations" should {

    "for the specified vrn" when {
      lazy val vrn = "555555555"

      "no query parameters are supplied" should {

        lazy val queryParams: VatObligationFilters = VatObligationFilters()

        val expected: String = "/vat-obligations/555555555/obligations"

        s"have the route '$expected'" in {
          val route = controllers.routes.VatObligationsController.getVatObligations(vrn, queryParams).url
          route shouldBe expected
        }
      }

      "'from' query parameter is supplied" should {

        lazy val queryParams: VatObligationFilters = VatObligationFilters(Some(stringToDate("2018-02-01")))

        val expected = "/vat-obligations/555555555/obligations?from=2018-02-01"

        s"have the route '$expected'" in {
          val route: String = controllers.routes.VatObligationsController.getVatObligations(vrn, queryParams).url
          route shouldBe expected
        }
      }

      "'from, to' query parameters are supplied" should {

        lazy val queryParams: VatObligationFilters = VatObligationFilters(
          Some(stringToDate("2018-02-01")), Some(stringToDate("2019-03-01")))

        val expected: String = "/vat-obligations/555555555/obligations?from=2018-02-01&to=2019-03-01"

        s"have the route '$expected'" in {
          val route = controllers.routes.VatObligationsController.getVatObligations(vrn, queryParams).url
          route shouldBe expected
        }
      }

      "all query parameters are supplied ('from, to, status')" should {

        lazy val queryParams: VatObligationFilters = VatObligationFilters(
          from = Some(stringToDate("2018-02-01")),
          to = Some(stringToDate("2019-03-01")),
          status = Some("F")
        )

        val expected: String = "/vat-obligations/555555555/obligations?from=2018-02-01&to=2019-03-01&status=F"

        s"have the route '$expected'" in {
          val route = controllers.routes.VatObligationsController.getVatObligations(vrn, queryParams).url
          route shouldBe expected
        }
      }
    }
  }
}
