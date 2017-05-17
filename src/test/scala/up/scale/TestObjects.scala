package up.scale

import up.scale.domain.in.{JobResponse, RelatedJob}
import up.scale.domain.out.Job

/**
  * Created by danylee on 16/05/17.
  */
object TestObjects {
  val related = List(
    RelatedJob("senior software engineer", 3),
    RelatedJob("intern", 1))

  val jobs = List(Job("senior software engineer", 3, "USD"),
    Job("intern", 1, "USD"))

  val jobResponse = JobResponse("software engineer", "USD", 2, related)
}
