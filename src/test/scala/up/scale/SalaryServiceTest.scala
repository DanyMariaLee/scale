/*
 * Copyright (c) 2017 Dany Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package up.scale

import org.scalatest.{Matchers, WordSpec}
import up.scale.serializers.Protocols
import TestObjects._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import up.scale.domain.out.Job

import scala.concurrent.Future

class SalaryServiceTest extends WordSpec
  with Matchers with Protocols with ScalaFutures with ServiceMocks {

  implicit val system = ActorSystem("test-salary-service")
  implicit val materializer = ActorMaterializer()

  "The salary service" should {

    "return one job" in new successful {
      val job = salaryService.fetchExact(Future.successful(Right(jobResponse)))
      job.futureValue should equal(Right(Job("software engineer", 2, "USD")))
    }

    "return related jobs" in new successful {
      val jbs = salaryService.fetchRelated(Future.successful(Right(jobResponse)))
      jbs.futureValue should equal(Right(jobs))
    }
  }
}