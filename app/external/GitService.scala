package external

import exceptions.{ForbiddenException, NotFoundException}
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.{JsArray, JsObject, JsValue}
import play.api.libs.ws.WSClient
import utlis.HeaderParser

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Class responsible for communication with the external Github API
 * @param wsClient
 * @param config
 * @param executionContext
 */
class GitService @Inject()(wsClient: WSClient, config: Configuration) (implicit val executionContext: ExecutionContext){
    import GitService._
    val optToken: Option[String] = config.getOptional[String]("git.token")

    def getRepositories(org: String): Future[Seq[JsArray]] = {
        val url = host+s"/orgs/$org/repos"
        getGeneric(url, Seq.empty)
    }
    def getContributorsPerRepo(fullName: String): Future[Seq[JsArray]] = {

        val url = host+s"/repos/$fullName/contributors"
        getGeneric(url, Seq.empty)
    }

    private def getGeneric(url: String, aggregator: Seq[JsArray]): Future[Seq[JsArray]] = {
        val wsRequest = optToken match {
            case Some(token) =>
                wsClient.url(url).withMethod("GET")
                    .withHttpHeaders(("Authorization", s"token $token"))
            case None => wsClient.url(url).withMethod("GET")

        }
       wsRequest.execute()
            .map {
                case r if r.status == Status.OK =>
                    val jsArray: JsArray = r.json.as[JsArray]
                    HeaderParser.getOptNextPage(r.headers) match {
                        case None => Future.successful(aggregator :+ jsArray)
                        case Some(nextUrl) =>
                           getGeneric(nextUrl, aggregator :+ jsArray)
                    }
                case r if r.status == Status.FORBIDDEN =>
                    val errorMessage = getErrorMessage(r.json)
                    throw ForbiddenException(errorMessage)
                case r if r.status == Status.NOT_FOUND =>
                    val errorMessage = getErrorMessage(r.json)
                    throw NotFoundException(s"Under url $url errorMessage: $errorMessage." +
                        " If mentioned url is the base one http://localhost:8080/org/{org_name}/contributors please " +
                        "check the value of {org_name} if its correct.")
            }.flatten
    }

    private def getErrorMessage(json: JsValue): String = json.as[JsObject].apply("message").as[String]
}

object GitService {
    val host = "https://api.github.com"
}
