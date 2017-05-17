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

/**
  * Created by danylee on 17/05/17.
  */
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
