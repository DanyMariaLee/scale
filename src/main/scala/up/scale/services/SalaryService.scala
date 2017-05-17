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

package up.scale.services

import java.net.{InetAddress, URLEncoder}

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, RemoteAddress}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model.StatusCodes.OK
import com.typesafe.config.ConfigFactory
import up.scale.domain.in.{GlassdoorResponse, JobResponse}
import up.scale.domain.out.{FailedResponse, Job, SuccessfulResponse}
import up.scale.routing.FlowResource
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.{ExecutionContext, Future}

class SalaryService(implicit val executionContext: ExecutionContext,
                    implicit val system: ActorSystem,
                    implicit val materializer: Materializer) extends FlowResource {

  val conf = ConfigFactory.load()

  val partnerId: String = conf.getString("glassdoor.partner-id")
  val gdKey: String = conf.getString("glassdoor.key")
  val gdApi: String = conf.getString("glassdoor.api")

  lazy val gdFlow: Flow[HttpRequest, HttpResponse, Any] = createFlow(gdApi)

  def getJob(jobTitle: String, ip: Option[RemoteAddress] = None): Future[Either[String, Job]] = {
    val jobResponse = getJobResponse(jobTitle, ip.flatMap(_.toOption), gdFlow)
    fetchExact(jobResponse)
  }

  def getRelated(jobTitle: String, ip: Option[RemoteAddress] = None): Future[Either[String, List[Job]]] = {
    val jobResponse = getJobResponse(jobTitle, ip.flatMap(_.toOption), gdFlow)
    fetchRelated(jobResponse)
  }

  def exact(jobTitle: String,
            ip: RemoteAddress): Future[ToResponseMarshallable] = {
    getJob(jobTitle, Option(ip)).map[ToResponseMarshallable] {
      case Right(job) => SuccessfulResponse(Left(job))
      case Left(msg) => FailedResponse(msg)
    }
  }

  def related(jobTitle: String,
              ip: RemoteAddress): Future[ToResponseMarshallable] = {
    getRelated(jobTitle, Option(ip)).map[ToResponseMarshallable] {
      case Right(related) => SuccessfulResponse(Right(related))
      case Left(msg) => FailedResponse(msg)
    }
  }

  protected def getJobResponse(jobTitle: String,
                     ip: Option[InetAddress] = None,
                     flow: Flow[HttpRequest, HttpResponse, Any]): Future[Either[String, JobResponse]] = {
    val salaryPath = getSalaryPath(jobTitle, ip)
    val salaryRequest = RequestBuilding.Get(salaryPath)
    val response = goByFlow(salaryRequest, flow)
    fetchJobResponse(response)
  }

  protected def fetchJobResponse(httpResponse: Future[HttpResponse]): Future[Either[String, JobResponse]] = {
    httpResponse.flatMap { response =>
      response.status match {
        case OK => Unmarshal(response.entity).to[GlassdoorResponse].map {
          case GlassdoorResponse(true, "OK", Some(body)) => Right(body)
          case _ => Left(s"No data in Glassdoor")
        }
        case _ => Future.successful(Left("Glassdoor API is unavailable"))
      }
    }.recoverWith {
      case _ => Future.successful(Left("Glassdoor API is unavailable"))
    }
  }

  protected def getSalaryPath(jobTitle: String,
                    ip: Option[InetAddress] = None): String = {
    val clientIp = ip.map(_.getCanonicalHostName).getOrElse("8.8.8.8")
    val jtEncoded = URLEncoder.encode(jobTitle.toLowerCase, "UTF-8")
    val params = "&useragent=&format=json&v=1&action=jobs-prog&jobTitle="
    s"""/api/api.htm?t.p=$partnerId&t.k=$gdKey&userip=$clientIp$params$jtEncoded"""
  }

  def fetchExact(jobResponseBody: Future[Either[String, JobResponse]]): Future[Either[String, Job]] = {
    jobResponseBody.map {
      case Right(body) => Right(Job(body.jobTitle, body.payMedian, body.payCurrencyCode))
      case Left(msg) => Left(msg)
    }
  }

  def fetchRelated(jobResponseBody: Future[Either[String, JobResponse]]): Future[Either[String, List[Job]]] = {
    jobResponseBody.map {
      case Right(body) => Right(fetchJobs(body))
      case Left(msg) => Left(msg)
    }
  }

  protected def fetchJobs(body: JobResponse): List[Job] = {
    val currency = body.payCurrencyCode
    body.results.map(r => Job(r.nextJobTitle, r.medianSalary, currency))
  }
}