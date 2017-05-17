package up.scale

import java.net.InetAddress

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import up.scale.domain.out.FailedResponse
import akka.stream.scaladsl.Flow
import up.scale.resources.GlassdoorResource
import up.scale.services.SalaryService
import up.scale.domain.in.JobResponse
import up.scale.domain.out.{Job, SuccessfulResponse}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import up.scale.serializers.Protocols
import TestObjects._

import scala.concurrent.Future

/**
  * Created by danylee on 16/05/17.
  */
class GlassdoorResourceTest extends WordSpec with Matchers
  with ScalatestRouteTest with Protocols {

  "The salary service" should {

    "return one job" in new successfulMock with GlassdoorResource {
      val uri = Uri("/salary").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
      Get(uri) ~> glassdoorRoutes ~> check {
        status shouldBe StatusCodes.OK

        entityAs[SuccessfulResponse] shouldEqual
          SuccessfulResponse(Left(Job("software engineer", 2, "USD")))
      }
    }

    "return related jobs" in new successfulMock with GlassdoorResource {
      val uri = Uri("/salary/related").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
      Get(uri) ~> glassdoorRoutes ~> check {
        status shouldBe StatusCodes.OK

        entityAs[SuccessfulResponse] shouldEqual
          SuccessfulResponse(Right(jobs))
      }
    }

    "return error from external service for related jobs request" in
      new failedMock with GlassdoorResource {

        val uri = Uri("/salary/related").withQuery(Uri.Query(Map("job_title" -> "software engineer")))
        Get(uri) ~> glassdoorRoutes ~> check {
          status shouldBe StatusCodes.OK

          entityAs[FailedResponse] shouldEqual
            FailedResponse("Some error", 1)
        }
      }

    "reject salary because of missing parameter job_title" in
      new failedMock with GlassdoorResource {
        val uri = Uri("/salary")
        Get(uri) ~> glassdoorRoutes ~> check {
          rejection shouldEqual MissingQueryParamRejection("job_title")
        }
      }

    "reject related salaries because of missing parameter job_title" in
      new failedMock with GlassdoorResource {

        val uri = Uri("/salary/related")
        Get(uri) ~> glassdoorRoutes ~> check {
          rejection shouldEqual MissingQueryParamRejection("job_title")
        }
      }

    "leave GET requests to other paths unhandled" in
      new failedMock with GlassdoorResource {

        val uri = Uri("/other")

        Get(uri) ~> glassdoorRoutes ~> check {
          handled shouldBe false
        }
      }
  }

  type FT = Flow[HttpRequest, HttpResponse, Any]
  type RT = Either[String, JobResponse]

  trait successfulMock {
    val salaryService = new SalaryService() {
      override def getJobResponse(jobTitle: String,
                                  ip: Option[InetAddress] = None,
                                  flow: FT): Future[RT] = {
        Future.successful(Right(jobResponse))
      }
    }
  }

  trait failedMock {
    val salaryService = new SalaryService() {
      override def getJobResponse(jobTitle: String,
                                  ip: Option[InetAddress] = None,
                                  flow: FT): Future[RT] = {
        Future.successful(Left("Some error"))
      }
    }
  }

}