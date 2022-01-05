/*
 * Copyright 2022 HM Revenue & Customs
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

package binders

import base.SpecBase
import models.VatObligationFilters
import VatObligationFilters._

class VatObligationsBindersSpec extends SpecBase {

  "The VatObligationsBinders.vatObligationsQueryBinder.bind method" should {

    "If no QueryParameters are passed" should {

      val queryParams: Map[String, Seq[String]] = Map("" -> Seq())

      "return an empty VatObligationFilters instance" in {

        val expected = Some(Right(VatObligationFilters()))
        val actual = VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }

    "If a dateFrom query parameter is passed" which {

      "is formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateFromKey -> Seq("2018-01-01"))

        "return an VatObligationFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatObligationFilters]] = Some(Right(VatObligationFilters(
            Some(stringToDate("2018-01-01")))))
          val actual: Option[Either[String, VatObligationFilters]] =
            VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(dateFromKey -> Seq("NoDate"))

        "return a bad request error message with details of the error" in {

          val expected: Option[Left[String, Nothing]] =
            Some(Left(s"Failed to bind '$dateFromKey=NoDate' valid date format should be 'YYYY-MM-DD'."))
          val actual: Option[Either[String, VatObligationFilters]] =
            VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "If a dateTo query parameter is passed" which {

        "is formatted correctly" should {

          val queryParams: Map[String, Seq[String]] = Map(dateToKey -> Seq("2018-01-01"))

          "return an VatObligationFilters instance with correct parameters" in {

            val expected: Option[Right[Nothing, VatObligationFilters]] =
              Some(Right(VatObligationFilters(None, Some(stringToDate("2018-01-01")))))
            val actual: Option[Either[String, VatObligationFilters]] =
              VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

            actual shouldBe expected
          }
        }

        "is formatted incorrectly" should {

          val queryParams: Map[String, Seq[String]] = Map(dateToKey -> Seq("NoDate"))

          "return a bad request error message with details of the error" in {

            val expected: Option[Left[String, Nothing]] =
              Some(Left(s"Failed to bind '$dateToKey=NoDate' valid date format should be 'YYYY-MM-DD'."))
            val actual: Option[Either[String, VatObligationFilters]] =
              VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

            actual shouldBe expected
          }
        }
      }
    }

    "If a status query parameter is passed" which {

      "is formatted correctly with F option" should {

        val queryParams: Map[String, Seq[String]] = Map(statusKey -> Seq("F"))

        "return an VatObligationFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatObligationFilters]] =
            Some(Right(VatObligationFilters(None, None, Some("F"))))
          val actual: Option[Either[String, VatObligationFilters]] =
            VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted correctly with O option" should {

        val queryParams: Map[String, Seq[String]] = Map(statusKey -> Seq("O"))

        "return an VatObligationFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatObligationFilters]] =
            Some(Right(VatObligationFilters(None, None, Some("O"))))
          val actual: Option[Either[String, VatObligationFilters]] =
            VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(statusKey -> Seq("Z"))

        "return a bad request error message with details of the error" in {

          val expected: Option[Left[String, Nothing]] =
            Some(Left(s"Failed to bind '$statusKey=Z' valid values are 'F' or 'O'."))
          val actual: Option[Either[String, VatObligationFilters]] =
            VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

    "If a all query parameters are passed" which {

      "are formatted correctly" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("2018-01-01"),
          dateToKey -> Seq("2018-01-01"),
          statusKey -> Seq("F")
        )

        "return an VatObligationFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatObligationFilters]] = Some(Right(VatObligationFilters(
            from = Some(stringToDate("2018-01-01")),
            to = Some(stringToDate("2018-01-01")),
            status = Some("F")
          )))

          val actual: Option[Either[String, VatObligationFilters]] = VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "if all are incorrectly formatted" should {

        val queryParams: Map[String, Seq[String]] = Map(
          dateFromKey -> Seq("NoDate"),
          dateToKey -> Seq("NoDate2"),
          statusKey -> Seq("Z")
        )

        "return a bad request error message with details of the error" in {

          val expected: Option[Left[String, Nothing]] = Some(Left(
            s"Failed to bind '$dateFromKey=NoDate' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$dateToKey=NoDate2' valid date format should be 'YYYY-MM-DD'., " +
              s"Failed to bind '$statusKey=Z' valid values are 'F' or 'O'."
          ))

          val actual = VatObligationsBinders.vatObligationsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }

  }

}
