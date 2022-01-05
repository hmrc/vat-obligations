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

package config

object ConfigKeys {

  val desServiceUrl: String = "microservice.services.des.url"
  val setupDesObligationsStartPath: String = "microservice.services.des.endpoints.vatObligationsUrlStart"
  val setupDesObligationsEndPath: String = "microservice.services.des.endpoints.vatObligationsUrlEnd"
  val desEnvironment: String = "microservice.services.des.environment"
  val desToken: String = "microservice.services.des.authorization-token"
}
