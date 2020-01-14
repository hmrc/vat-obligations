/*
 * Copyright 2020 HM Revenue & Customs
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

import base.SpecBase
import VatObligationFilters._

class VatObligationFiltersSpec extends SpecBase {

  "The VatObligationFilters object" should {

    "have the correct key value for 'dateFrom'" in {
      dateFromKey shouldBe "from"
    }

    "have the correct key value for 'dateTo'" in {
      dateToKey shouldBe "to"
    }

    "have the correct key value for 'onlyOpenItems'" in {
      statusKey shouldBe "status"
    }
  }

  "The VatObligationFilters.toSeqQueryParams method" should {

    "output the expected sequence of key-value pairs" which {

      "for no Query Parameters, has no values" in {
        val queryParams: VatObligationFilters = VatObligationFilters()
        queryParams.toSeqQueryParams shouldBe Seq()
      }

      "for from Query Param, has a 'dateFrom' param with correct value" in {
        val queryParams: VatObligationFilters = VatObligationFilters(from = Some(stringToDate("2018-04-06")))
        queryParams.toSeqQueryParams shouldBe Seq(dateFromKey -> "2018-04-06")
      }

      "for to Query Param, has a 'dateTo' param with correct value" in {
        val queryParams: VatObligationFilters = VatObligationFilters(to = Some(stringToDate("2019-04-05")))
        queryParams.toSeqQueryParams shouldBe Seq(dateToKey -> "2019-04-05")
      }

      "for status Query Param, has a 'status' param with correct value" in {
        val queryParams: VatObligationFilters = VatObligationFilters(status = Some("F"))
        queryParams.toSeqQueryParams shouldBe Seq(statusKey -> "F")
      }

      "for all Query Params, outputs them all as expected" in {
        val queryParams: VatObligationFilters = VatObligationFilters(
          from = Some(stringToDate("2017-04-06")),
          to = Some(stringToDate("2018-04-05")),
          status = Some("F")
        )
        queryParams.toSeqQueryParams shouldBe Seq(
          dateFromKey -> "2017-04-06",
          dateToKey -> "2018-04-05",
          statusKey -> "F"
        )
      }
    }
  }
}
