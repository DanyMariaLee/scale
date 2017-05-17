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
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import up.scale.domain.out.FailedResponse
import up.scale.resources.GlassdoorResource
import up.scale.domain.out.{Job, SuccessfulResponse}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import up.scale.serializers.Protocols
import TestObjects._

class GlassdoorResourceTest extends WordSpec with Matchers
  with ScalatestRouteTest with Protocols with ServiceMocks {

  "The salary service" should {

    "return one job" in new successful with GlassdoorResource {
      val uri = Uri("/salary").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
      Get(uri) ~> glassdoorRoutes ~> check {
        status shouldBe StatusCodes.OK

        entityAs[SuccessfulResponse] shouldEqual
          SuccessfulResponse(Left(Job("software engineer", 2, "USD")))
      }
    }

    "return related jobs" in new successful with GlassdoorResource {
      val uri = Uri("/salary/related").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
      Get(uri) ~> glassdoorRoutes ~> check {
        status shouldBe StatusCodes.OK

        entityAs[SuccessfulResponse] shouldEqual
          SuccessfulResponse(Right(jobs))
      }
    }

    "return error from external service for related jobs request" in
      new failed with GlassdoorResource {

        val uri = Uri("/salary/related").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
        Get(uri) ~> glassdoorRoutes ~> check {
          status shouldBe StatusCodes.OK

          entityAs[FailedResponse] shouldEqual
            FailedResponse("Some error", 1)
        }
      }

    "reject salary because of missing parameter job_title" in
      new failed with GlassdoorResource {
        val uri = Uri("/salary")
        Get(uri) ~> glassdoorRoutes ~> check {
          rejection shouldEqual MissingQueryParamRejection("job_title")
        }
      }

    "reject related salaries because of missing parameter job_title" in
      new failed with GlassdoorResource {

        val uri = Uri("/salary/related")
        Get(uri) ~> glassdoorRoutes ~> check {
          rejection shouldEqual MissingQueryParamRejection("job_title")
        }
      }

    "leave GET requests to other paths unhandled" in
      new failed with GlassdoorResource {

        val uri = Uri("/other")

        Get(uri) ~> glassdoorRoutes ~> check {
          handled shouldBe false
        }
      }
  }
}