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

import uk.gov.hmrc.mtdtransactionriskingstub.models.StubObligation
import uk.gov.hmrc.mtdtransactionriskingstub.utils.{StubPeriodKeys, ValidationStubError}

// Resolves a period key to an open obligation
object ObligationStubService:

  def lookupByPeriodKey(periodKey: String): Either[ValidationStubError, StubObligation] =
    periodKey match
      case StubPeriodKeys.periodKeyNotFound => Left(periodKeyNotFoundError)
      case StubPeriodKeys.taxPeriodNotEnded => Left(taxPeriodNotEndedError)
      case _                                => Right(endedObligation(periodKey))

  private def endedObligation(periodKey: String): StubObligation =
    StubObligation(periodKey, start = "2026-01-01", end = "2026-03-31", due = "2026-05-07")

  private val taxPeriodNotEndedError =
    ValidationStubError("TAX_PERIOD_NOT_ENDED", "Tax period not ended", None, selfWraps = false)

  private val periodKeyNotFoundError =
    ValidationStubError("PERIOD_KEY_NOT_FOUND", "Period key not found", None, selfWraps = false)