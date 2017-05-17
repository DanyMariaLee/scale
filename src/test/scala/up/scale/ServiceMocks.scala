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

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import up.scale.TestObjects.jobResponse
import up.scale.domain.in.JobResponse
import up.scale.services.SalaryService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ServiceMocks {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  type FT = Flow[HttpRequest, HttpResponse, Any]
  type RT = Either[String, JobResponse]

  trait successful {
    val salaryService = new SalaryService() {
      override def getJobResponse(jobTitle: String,
                                  ip: Option[InetAddress] = None,
                                  flow: FT): Future[RT] = {
        Future.successful(Right(jobResponse))
      }
    }
  }

  trait failed {
    val salaryService = new SalaryService() {
      override def getJobResponse(jobTitle: String,
                                  ip: Option[InetAddress] = None,
                                  flow: FT): Future[RT] = {
        Future.successful(Left("Some error"))
      }
    }
  }

}
