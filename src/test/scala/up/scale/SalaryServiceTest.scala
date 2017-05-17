package up.scale

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import org.scalatest.{Matchers, WordSpec}
import up.scale.domain.in.JobResponse
import up.scale.serializers.Protocols
import up.scale.services.SalaryService
import TestObjects._
import org.scalatest.concurrent.ScalaFutures
import up.scale.domain.out.Job

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by danylee on 16/05/17.
  */
class SalaryServiceTest extends WordSpec
  with Matchers with Protocols with ScalaFutures {

  implicit val system = ActorSystem("test-salary-service")
  implicit val materializer = ActorMaterializer()

  "The salary service" should {

    "return one job" in new successfulMock {
      val job = salaryService.fetchExact(Future.successful(Right(jobResponse)))
      job.futureValue should equal(Right(Job("software engineer", 2, "USD")))
    }

    "return related jobs" in new successfulMock {
      val jbs = salaryService.fetchRelated(Future.successful(Right(jobResponse)))
      jbs.futureValue should equal(Right(jobs))
    }


    /*    "return successful response with jobs" in new successfulMock {
          val jbs = salaryService.getJob("software engineer", RemoteAddress.Unknown)
          Unmarshal(jbs).to[SuccessfulResponse].futureValue should equal(Right(related))
        }*/
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