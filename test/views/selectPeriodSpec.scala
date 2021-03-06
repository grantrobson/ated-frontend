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

package views

import java.util.UUID

import builders.AuthBuilder._
import forms.AtedForms.selectPeriodForm
import org.joda.time.LocalDate
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FeatureSpec, GivenWhenThen}
import org.scalatestplus.play.OneServerPerSuite
import play.api.test.FakeRequest
import utils.{PeriodUtils, AtedUtils}

class selectPeriodSpec extends FeatureSpec with OneServerPerSuite with MockitoSugar with BeforeAndAfterEach with GivenWhenThen{

  implicit val request = FakeRequest()
  implicit val messages : play.api.i18n.Messages = play.api.i18n.Messages.Implicits.applicationMessages
  val userId = s"user-${UUID.randomUUID}"
  implicit val user = createAtedContext(createUserAuthContext(userId, "name"))
  val periodKey = 2015

  feature("The user can view the select period page") {

    info("As a client I want to be able to select what period I am submitting a return for")

    scenario("Show the select period radio buttons") {

      Given("The client is creating a new return and wants to tell us the year it is for")
      When("The user views the page")

      val periods = PeriodUtils.getPeriods(new LocalDate(2015,4,1), new LocalDate(2016,4,1))
      val html = views.html.selectPeriod(selectPeriodForm, periods, Some("http://backLink"))

      val document = Jsoup.parse(html.toString())

      Then("The header should match - Select an ATED period")
      assert(document.getElementById("header").text === "Select an ATED chargeable period")

      Then("The subheader should be - Create relief return")
      assert(document.getElementById("pre-heading").text() === "This section is: Create return")

      Then("The the text on the screen should be correct")
      assert(document.getElementById("details-text").text() === "The chargeable period for a year runs from the 1 April to 31 March.")
      assert(document.getElementById("showMoreYears").text() === "I want to submit a return before 2015")
      assert(document.getElementById("showMoreYearsAnswer").text() === "Any ATED returns before these periods need to be submitted on a paper form. To request a paper form contact the call centre.")
      assert(document.getElementById("period-2015_field").text() == "2015 to 2016")
      assert(document.getElementById("period-2016_field").text() == "2016 to 2017")
      assert(document.getElementById("period-2017_field") == null)

      assert(document.getElementById("submit").text() === "Continue")

      assert(document.getElementById("backLinkHref").text === "Back")
      assert(document.getElementById("backLinkHref").attr("href") === "http://backLink")
    }
  }

}
