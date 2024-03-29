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

import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

val appName = "vat-obligations"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty
val bootstrapPlayVersion = "7.15.0"

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "Reverse.*",
    "app.*",
    "prod.*",
    "config.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

val compile = Seq(
  ws,
  "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
  "com.typesafe.play" %% "play-json-joda"            % "2.10.0-RC7"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlayVersion % scope,
  "org.jsoup"              %  "jsoup"                  % "1.15.3"             % scope,
  "org.scalatestplus"      %% "mockito-3-3"            % "3.2.2.0"            % scope
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = tests map {
  test => Group(test.name, Seq(test), SubProcess(
    ForkOptions().withRunJVMOptions(Vector("-Dtest.name=" + test.name))))
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala,SbtDistributablesPlugin) ++ plugins : _*)
  .settings(coverageSettings: _*)
  .settings(playSettings : _*)
  .settings(scalaSettings: _*)
  .settings(majorVersion := 0)
  .settings(defaultSettings(): _*)
  .settings(scalacOptions ++= Seq("-Wconf:cat=unused-imports&src=.*routes.*:s"))
  .settings(
    scalaVersion := "2.13.8",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    routesImport += "binders.VatObligationsBinders._",
    PlayKeys.playDefaultPort := 9155
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    IntegrationTest / parallelExecution := false
  )
