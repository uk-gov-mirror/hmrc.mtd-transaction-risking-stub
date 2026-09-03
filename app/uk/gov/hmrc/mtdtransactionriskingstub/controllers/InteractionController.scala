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

import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

// Stubs the interactions datastore which reached once RDS succeeds
@Singleton
class InteractionController @Inject()(cc: ControllerComponents) extends BackendController(cc), Logging:

  private val badRequestTrigger        = "rsd-bad-request"
  private val serverErrorTrigger       = "rsd-server-error"
  private val serviceUnavailableTrigger = "rsd-unavailable"

  def store(): Action[JsValue] = Action.async(parse.json) { request =>

    val correlationId = request.headers.get("CorrelationId").getOrElse("no-correlation-id")
    val feedbackId    = (request.body \ "feedbackId").asOpt[String].getOrElse("")

    logger.info(s"$correlationId::[InteractionController][store] received interaction for feedbackId $feedbackId")

    val result = feedbackId match

      case `badRequestTrigger` =>
        BadRequest(Json.obj(
          "error"     -> "Bad Request",
          "errors"    -> Json.arr(Json.obj("error" -> "eventName must not be blank")),
          "path"      -> "/rsd/receive-and-store",
          "status"    -> 400,
          "timestamp" -> "2026-08-13T09:00:00Z"
        ))

      case `serverErrorTrigger` =>
        InternalServerError(Json.obj())

      case `serviceUnavailableTrigger` =>
        ServiceUnavailable(hipOriginResponse("internal", "service temporarily unavailable"))
          .as("application/json;charset=UTF-8")

      case _ =>
        NoContent

    Future.successful(result)
  }

  private def hipOriginResponse(failureType: String, reason: String): JsValue =
    Json.obj(
      "origin"   -> "HIP",
      "response" -> Json.obj("failures" -> Json.arr(Json.obj("type" -> failureType, "reason" -> reason)))
    )
    