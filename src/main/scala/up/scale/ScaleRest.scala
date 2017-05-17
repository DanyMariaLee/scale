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

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import up.scale.resources.GlassdoorResource
import up.scale.services.SalaryService

import scala.concurrent.ExecutionContext

/**
  * Created by danylee on 16/05/17.
  */
trait ScaleRest extends GlassdoorResource {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val executionContext: ExecutionContext

  lazy val salaryService = new SalaryService

  val routes: Route = glassdoorRoutes
}
