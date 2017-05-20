import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.body.StringBody

/**
  * Created by Daniel on 20.05.2017.
  */
class SimTest extends Simulation {


  object QueryProcess {

    val headers_10 = Map("Content-Type" -> "application/json")

    val search = exec(http("GET")
      .get("/cam/process/SOHBSS-000113"))
      .pause(1)
      .exec(http("Post")
        .post("/cam/process/query-instances")
        .headers(headers_10)
        .body(StringBody(
          """{
             "query": {
                    "referrerOrganisationUid": {
                           "value": "stark",
                           "operator": "EQ"
                    }
             }
          }"""))
        .check(jsonPath("$.result.data[0].id").saveAs("myresponse"))).exec(session => {
      val maybeId = session.get("myresponse").asOption[String]
      println(maybeId.getOrElse("COULD NOT FIND ID"))
      session
    })

  }


  val httpConf = http
    .baseURL("http://localhost:8280")
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Query Processes").exec(QueryProcess.search)

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

}
