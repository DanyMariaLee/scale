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

package up.scale.resources

import akka.http.scaladsl.server.Route
import up.scale.services.SalaryService
import akka.http.scaladsl.server.Directives._

trait GlassdoorResource {

  val salaryService: SalaryService

  def glassdoorRoutes: Route = {
    pathPrefix("salary") {
      (get & extractClientIP) { ip =>
        parameter('job_title) { jobTitle =>
          pathEndOrSingleSlash {
            complete {
              salaryService.exact(jobTitle, ip)
            }
          } ~ path("related") {
            complete {
              salaryService.related(jobTitle, ip)
            }
          }
        }
      }
    }
  }
}