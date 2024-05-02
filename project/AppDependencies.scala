/*
 * Copyright 2017 HM Revenue & Customs
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

import CompileDependencies.bootstrapVersion
import sbt._

object AppDependencies {
  def apply(): Seq[ModuleID] = CompileDependencies() ++ UnitTestDependencies()

  val it: Seq[ModuleID] = Seq()

}

object CompileDependencies {
  val bootstrapVersion = "8.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "com.typesafe.play" %% "play-json-joda"            % "2.10.0-RC7"
  )

  def apply(): Seq[ModuleID] = compile
}

object UnitTestDependencies extends CommonTestDependencies {
  override val scope: Configuration = Test

  override val test: Seq[ModuleID] = Seq(
    "org.jsoup"         %  "jsoup"                    % "1.17.2"             % scope,
    "org.scalatestplus" %% "mockito-4-2"              % "3.2.11.0"           % scope,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"   % bootstrapVersion     % scope
  )

  def apply(): Seq[ModuleID] = test
}

trait CommonTestDependencies {
  val scope: Configuration
  val test: Seq[ModuleID]
}
