package up.scale


import org.scalatest.{Matchers, WordSpec}
import up.scale.serializers.Protocols
import TestObjects._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import up.scale.domain.out.Job

import scala.concurrent.Future

/**
  * Created by danylee on 16/05/17.
  */
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