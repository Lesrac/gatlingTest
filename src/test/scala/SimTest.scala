import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.body.StringBody

/**
  * Created by Daniel on 20.05.2017.
  */
class SimTest extends Simulation {


  object QueryProcess {

    val headers_10 = Map("Content-Type" -> "application/json")

    var kindredId = -1

    val search =
      exec(http("GET Kindreds")
        .get("/kindred")
        .check(status.is(200), jsonPath("$[0]['id']").saveAs("kindredId")))
        .pause(1)
        .exec(session => {
          val id = session.get("kindredId").asOption[String]
          println(id.getOrElse("COULD NOT FIND ID"))
          session
        })

  }

  val httpConf = http
    .baseURL("http://localhost:8080/Darklands_Host_Builder")
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Query Processes").exec(QueryProcess.search)

  setUp(scn.inject(atOnceUsers(5)).protocols(httpConf))

}
