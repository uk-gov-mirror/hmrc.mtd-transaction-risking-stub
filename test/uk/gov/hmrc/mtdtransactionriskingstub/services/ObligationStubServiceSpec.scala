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

import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.mtdtransactionriskingstub.utils.StubPeriodKeys

class ObligationStubServiceSpec extends AnyWordSpec, Matchers, EitherValues:


  "lookupByPeriodKey" should :

    "return PERIOD_KEY_NOT_FOUND for the reserved not-found key" in :
      ObligationStubService.lookupByPeriodKey(StubPeriodKeys.periodKeyNotFound).left.value.code shouldBe "PERIOD_KEY_NOT_FOUND"

    "return TAX_PERIOD_NOT_ENDED for the reserved not-ended key" in :
      ObligationStubService.lookupByPeriodKey(StubPeriodKeys.taxPeriodNotEnded).left.value.code shouldBe "TAX_PERIOD_NOT_ENDED"

    "return an ended obligation for any other key" in :
      val obligation = ObligationStubService.lookupByPeriodKey("AB12").value

      obligation.periodKey shouldBe "AB12"
      obligation.end shouldBe "2026-03-31"

    "echo the requested period key back on the obligation" in :
      ObligationStubService.lookupByPeriodKey("XY45").value.periodKey shouldBe "XY45"

    "return an ended obligation for the RDS trigger keys, so requests reach RDS" in :
      val rdsKeys = Seq(
        StubPeriodKeys.rdsBadRequest,
        StubPeriodKeys.rdsNotFound,
        StubPeriodKeys.rdsServiceUnavailable,
        StubPeriodKeys.rdsNoFeedback,
        StubPeriodKeys.rdsMalformedReport
      )

      rdsKeys.foreach { key =>
        withClue(s"for period key $key: ")(ObligationStubService.lookupByPeriodKey(key).value.periodKey shouldBe key)
      }