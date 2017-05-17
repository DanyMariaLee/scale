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

import up.scale.domain.in.{JobResponse, RelatedJob}
import up.scale.domain.out.Job

object TestObjects {
  val related = List(
    RelatedJob("senior software engineer", 3),
    RelatedJob("intern", 1))

  val jobs = List(Job("senior software engineer", 3, "USD"),
    Job("intern", 1, "USD"))

  val jobResponse = JobResponse("software engineer", "USD", 2, related)
}
