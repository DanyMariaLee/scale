
#Scale
 
 ![alt text](src/main/resources/logo.png)

An example of [Akka HTTP 2.5.1](http://akka.io/docs/?_ga=2.132477901.2114488730.1495028763-1586046796.1493973640) library usage for building a REST service.
The service itself takes median salary for the requested job title or related jobs from Glassdoor API. 

# What's implemented
- routing, marshaling, unmarshalling
- requests to external API of [Glassdoor](www.glassdoor.com)
- testing with [akka-http-testkit](http://doc.akka.io/docs/akka-http/10.0.5/scala/http/routing-dsl/testkit.html) and [scalatest](http://www.scalatest.org/) (ScalaFutures included)
- publishing to [Sonatype](oss.sonatype.org)


### application.conf
Since Glassdoor provides information only for registered application we need to add keys to apps configuration.
To get keys go to [Glassdoor API page](www.glassdoor.com/developer/index.htm) to register your application. 
Configuration in your app.conf file will look like this
```sh
glassdoor {
  partner-id = "111111"
  key =	"asdffff111"
}
 ```
 
### build.sbt 

If you want to use it as an external library by calling a service directly or adding the routes
```scala
"com.github.danymarialee" %% "scale" % "1.0.0"
 ```


### Usage by getting the code from github
```sbtshell
sbt
> run
 ```
Then you'll see something like 
```sbtshell
[info] Running up.scale.Main 
[scale-service-akka.actor.default-dispatcher-5] INFO akka.event.slf4j.Slf4jLogger - Slf4jLogger started
```
Now you cal call the service to get median salary
```sbtshell
curl http://localhost:9000/salary?job_title=driver
```
=> 
```json
{
   "data": {
       "title": "driver",
       "payMedian": 31000,
       "payCurrencyCode": "USD"
   },
   "code": 0
}
```

Or for related job titles
```sbtshell
curl http://localhost:9000/salary/related?job_title=driver
```
=>
```json
{
    "data": [
       {
         "title": "truck driver",
         "payMedian": 46000,
         "payCurrencyCode": "USD"
       },
       {
         "title": "delivery driver",
         "payMedian": 39000,
         "payCurrencyCode": "USD"
       },
       {
         "title": "bus driver",
         "payMedian": 26500,
         "payCurrencyCode": "USD"
       }
    ],
    "code": 0
}
```

### Usage by calling with SalaryService
The SalaryService requires 

- ExecutionContext
- ActorSystem
- Materializer

Those are implicits => means need to be in the scope

```scala
import up.scale.services.SalaryService

val service = new SalaryService()

val exact = service.getJob("writer") // => Job
val related = service.getRelated("writer") // => List[Job]
```

### Domain
```scala
case class Job(title: String, payMedian: Double, payCurrencyCode: String)
```

### Testing
```sbtshell
sbt
> test
```
=>
```sbtshell
[test-salary-service-akka.actor.default-dispatcher-4] INFO akka.event.slf4j.Slf4jLogger - Slf4jLogger started
[info] SalaryServiceTest:
[info] The salary service
[info] - should return one job
[info] - should return related jobs
[info] GlassdoorResourceTest:
[info] The salary service
[info] - should return one job
[info] - should return related jobs
[info] - should return error from external service for related jobs request
[info] - should reject salary because of missing parameter job_title
[info] - should reject related salaries because of missing parameter job_title
[info] - should leave GET requests to other paths unhandled
[info] Run completed in 4 seconds, 102 milliseconds.
[info] Total number of tests run: 8
[info] Suites: completed 2, aborted 0
[info] Tests: succeeded 8, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 5 s, completed May 17, 2017 5:43:56 PM
```

If you have any questions my Twitter handle [@besseifunction](https://twitter.com/besseifunction)

### Have fun!

