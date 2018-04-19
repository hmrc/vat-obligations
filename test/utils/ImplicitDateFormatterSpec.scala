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

package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

import base.SpecBase
import utils.ImplicitDateFormatter._

class ImplicitDateFormatterSpec extends SpecBase {

  "The implicit date formatter" should {

    "format string dates" in {
      val result: LocalDate = "2017-04-01"
      result shouldBe LocalDate.of(2017, 4, 1)
    }

    "format months with single digit values" in {
      val result: LocalDate = "2017-6-30"
      result shouldBe LocalDate.of(2017, 6, 30)
    }

    "format days with single digit values" in {
      val result: LocalDate = "2017-6-1"
      result shouldBe LocalDate.of(2017, 6, 1)
    }

    "format string DateTimes" in {
      val result: ZonedDateTime = "2017-04-01T11:23:45.123Z"
      result shouldBe ZonedDateTime.parse("2017-04-01T11:23:45.123Z", DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
  }
}
