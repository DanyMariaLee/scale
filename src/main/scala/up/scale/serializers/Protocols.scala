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

package up.scale.serializers

import spray.json.DefaultJsonProtocol
import up.scale.domain.in.{GlassdoorResponse, JobResponse, RelatedJob}
import up.scale.domain.out.{FailedResponse, Job, SuccessfulResponse}


trait Protocols extends DefaultJsonProtocol {

  implicit val jResultFormat = jsonFormat2(RelatedJob.apply)
  implicit val jrbFormat = jsonFormat4(JobResponse.apply)
  implicit val jResponseFormat = jsonFormat3(GlassdoorResponse.apply)
  implicit val sFormat = jsonFormat3(Job.apply)
  implicit val scrFormat = jsonFormat2(SuccessfulResponse.apply)
  implicit val fcrFormat = jsonFormat2(FailedResponse.apply)

}