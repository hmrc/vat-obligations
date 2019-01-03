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

package helpers.servicemocks

import binders.VatObligationsBinders
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper._
import models.VatObligationFilters
import play.api.libs.json.JsValue

object DesVatObligationsStub {

  private def vatObligationsUrl(vrn: String, queryParameters: VatObligationFilters): String = {
    if (queryParameters.hasQueryParameters) {
      s"/enterprise/obligation-data/vrn/$vrn/VATC" +
        s"?${VatObligationsBinders.vatObligationsQueryBinder.unbind("", queryParameters)}"
    }
    else {
      s"/enterprise/obligation-data/vrn/$vrn/VATC"
    }
  }

  def stubGetVatObligations(vrn: String, queryParams: VatObligationFilters)(status: Int, response: JsValue): StubMapping = {
    stubGet(vatObligationsUrl(vrn, queryParams), status, response.toString())
  }

  def verifyGetVatObligations(vrn: String, queryParams: VatObligationFilters): Unit = {
    verifyGet(vatObligationsUrl(vrn, queryParams))
  }

}

