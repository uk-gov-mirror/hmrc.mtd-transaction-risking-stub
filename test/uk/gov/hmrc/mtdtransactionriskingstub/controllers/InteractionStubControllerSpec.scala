/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.mtdtransactionriskingstub.controllers

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, contentType, defaultAwaitTimeout, status, stubControllerComponents}

class InteractionStubControllerSpec extends AnyWordSpec, Matchers:

  private given system: ActorSystem  = ActorSystem("test")
  private given mat:    Materializer = Materializer(system)

  private val controller = new InteractionStubController(stubControllerComponents())

  private def interactionJson(feedbackId: String): JsValue = Json.parse(
    s"""
       |{
       |  "serviceRegime": "vat-assist",
       |  "eventName": "generate-report",
       |  "feedbackId": "$feedbackId",
       |  "eventTimestamp": "2026-08-13T09:00:00Z",
       |  "metadata": [
       |    {
       |      "vrn": "123456789",
       |      "start": "2026-01-01",
       |      "end": "2026-03-31",
       |      "additionalProperties": { "periodKey": "AB12" }
       |    }
       |  ],
       |  "payload": {
       |    "reportId": "$feedbackId",
       |    "messages": []
       |  }
       |}
       |""".stripMargin
  )

  private def requestWith(feedbackId: String, correlationId: Option[String] = None) =
    val base = FakeRequest("POST", "/rsd/receive-and-store").withBody(interactionJson(feedbackId))
    correlationId.fold(base)(id => base.withHeaders("CorrelationId" -> id))

  "store" should:

    "return 204 for a normal feedbackId" in:
      val result = controller.store()(requestWith("f2fb30e5-4ab6-4a29-b3c1-c00000000001"))

      status(result) shouldBe NO_CONTENT

    "return 400 with the ErrorMessages shape when the feedbackId is rsd-bad-request" in:
      val result = controller.store()(requestWith("rsd-bad-request"))

      status(result) shouldBe BAD_REQUEST
      (contentAsJson(result) \ "status").as[Int] shouldBe 400
      (contentAsJson(result) \ "errors").as[Seq[JsValue]] should not be empty

    "return 500 with an empty body when the feedbackId is rsd-server-error" in:
      val result = controller.store()(requestWith("rsd-server-error"))

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj()

    "return 503 with the HIP-originResponse shape when the feedbackId is rsd-unavailable" in:
      val result = controller.store()(requestWith("rsd-unavailable"))

      status(result) shouldBe SERVICE_UNAVAILABLE
      contentType(result) shouldBe Some("application/json")
      (contentAsJson(result) \ "origin").as[String] shouldBe "HIP"
      (contentAsJson(result) \ "response" \ "failures").as[Seq[JsValue]] should not be empty

    "accept a request with no CorrelationId header" in:
      val result = controller.store()(requestWith("f2fb30e5-4ab6-4a29-b3c1-c00000000001", correlationId = None))

      status(result) shouldBe NO_CONTENT