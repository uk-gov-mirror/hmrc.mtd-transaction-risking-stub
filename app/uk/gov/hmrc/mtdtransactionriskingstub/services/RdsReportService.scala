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

package uk.gov.hmrc.mtdtransactionriskingstub.services

import play.api.libs.json.{JsValue, Json}

object RdsReportService:

  val badRequestKey         = "RD01"
  val notFoundKey           = "RD02"
  val serviceUnavailableKey = "RD03"
  val noFeedbackKey         = "RD04"
  val malformedKey          = "RD05"

  def reportFor(feedbackId: String, correlationId: String, responseCode: String = "201"): JsValue = Json.parse(
    s"""
       |{
       |  "links": [],
       |  "version": 2,
       |  "moduleId": "rbfConceptHub",
       |  "stepId": "execute",
       |  "executionState": "completed",
       |  "outputs": [
       |    { "name": "createdDttm", "value": "2026-04-01T09:35:15.094Z" },
       |    { "name": "correlationId", "value": "$correlationId" },
       |    { "name": "feedbackId", "value": "$feedbackId" },
       |    { "name": "responseCode", "value": "$responseCode" },
       |    { "name": "responseMessage", "value": "Feedback generated successfully" },
       |    {
       |      "name": "englishActions",
       |      "value": [
       |        { "metadata": [ { "itemNumber": "1" }, { "message": "m" }, { "action": "a" }, { "title": "t" },
       |                        [ { "linkTitle": "lt" }, { "linkUrl": "lu" } ], { "path": "p" } ] },
       |        { "data": [ [ "1", "Please review your VAT return figures.", "Check your sales records for the period.",
       |                      "VAT Return Query",
       |                      [ { "linkTitle": "VAT guidance" }, { "linkUrl": "https://www.gov.uk/vat-returns" } ],
       |                      "vatDueSales" ] ] }
       |      ]
       |    },
       |    {
       |      "name": "welshActions",
       |      "value": [
       |        { "metadata": [ { "itemNumber": "1" }, { "message": "m" }, { "action": "a" }, { "title": "t" },
       |                        [ { "linkTitle": "lt" }, { "linkUrl": "lu" } ], { "path": "p" } ] },
       |        { "data": [ [ "1", "Adolygwch eich ffigurau Ffurflen TAW.", "Gwiriwch eich cofnodion gwerthu.",
       |                      "Ymholiad Ffurflen TAW",
       |                      [ { "linkTitle": "Canllawiau TAW" }, { "linkUrl": "https://www.gov.uk/ffurflenni-taw" } ],
       |                      "vatDueSales" ] ] }
       |      ]
       |    }
       |  ]
       |}
       |""".stripMargin
  )

  // A report with no feedback messages
  def emptyReportFor(feedbackId: String, correlationId: String): JsValue = Json.parse(
    s"""
       |{
       |  "outputs": [
       |    { "name": "correlationId", "value": "$correlationId" },
       |    { "name": "feedbackId", "value": "$feedbackId" },
       |    { "name": "responseCode", "value": "201" },
       |    { "name": "responseMessage", "value": "No feedback applicable" },
       |    { "name": "englishActions", "value": [ { "metadata": [] }, { "data": [] } ] },
       |    { "name": "welshActions", "value": [ { "metadata": [] }, { "data": [] } ] }
       |  ]
       |}
       |""".stripMargin
  )
