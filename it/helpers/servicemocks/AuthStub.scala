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

package helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper
import play.api.http.Status

object AuthStub {

  val postAuthoriseUrl: String = "/auth/authorise"

  def stubAuthorised(): StubMapping =
    WiremockHelper.stubPost(postAuthoriseUrl, Status.OK, """{"externalId": "1234"}""")

  def stubUnauthorised(): StubMapping =
    WiremockHelper.stubPost(postAuthoriseUrl, Status.UNAUTHORIZED, """{"externalId": "1234"}""")
}
