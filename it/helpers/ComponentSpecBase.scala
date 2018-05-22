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

package helpers

import binders.VatObligationsBinders
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.servicemocks.AuthStub
import models.VatObligationFilters
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSResponse
import play.api.{Application, Environment, Mode}

trait ComponentSpecBase extends TestSuite with CustomMatchers
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience with Matchers
  with WiremockHelper with BeforeAndAfterEach with BeforeAndAfterAll with Eventually {

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"
  val mockToken = "localToken"
  val mockEnvironment = "localEnvironment"
  val mockEndpointStart = "/enterprise/obligation-data/vrn/"
  val mockEndpointEnd = "/VATC"

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.des.host" -> mockHost,
    "microservice.services.des.port" -> mockPort,
    "microservice.services.des.endpoints.vatObligationsUrlStart" -> mockEndpointStart,
    "microservice.services.des.endpoints.vatObligationsUrlEnd" -> mockEndpointEnd,
    "microservice.services.des.environment" -> mockEnvironment,
    "microservice.services.des.authorization-token" -> mockToken
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  object VatObligationsComponent {
    def get(uri: String): WSResponse = await(buildClient(uri).get())

    def getVatObligations(vrn: String, queryParameters: VatObligationFilters): WSResponse =
      get(s"/vat-obligations/$vrn/obligations${VatObligationsBinders.vatObligationsQueryBinder.unbind("", queryParameters)}")
  }
}