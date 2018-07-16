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

package config

import javax.inject.{Inject, Singleton}

import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig
import config.{ConfigKeys => Keys}

trait AppConfig extends ServicesConfig {
  val desEnvironment: String
  val desToken: String
  val desServiceUrl: String
  val setupDesObligationsStartPath: String
  val setupDesObligationsEndPath: String
}

@Singleton
class MicroserviceAppConfig @Inject()(val environment: Environment, val conf: Configuration) extends AppConfig {

  override protected def runModeConfiguration: Configuration = conf
  override protected def mode: Mode = environment.mode
  private def loadConfig(key: String) = runModeConfiguration.getString(key)
    .getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val appName: String = loadConfig("appName")

  override lazy val desEnvironment: String = loadConfig(Keys.desEnvironment)
  override lazy val desToken: String = loadConfig(Keys.desToken)
  override lazy val desServiceUrl: String = loadConfig(Keys.desServiceUrl)
  override lazy val setupDesObligationsStartPath: String = loadConfig(Keys.setupDesObligationsStartPath)
  override lazy val setupDesObligationsEndPath: String = loadConfig(Keys.setupDesObligationsEndPath)

}
