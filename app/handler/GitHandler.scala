package handler

import external.GitService
import handler.GitHandler.fullName
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue}
import play.api.mvc.Result
import play.api.mvc.Results.Ok

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
 * Class for handling request to git repository
 * @param gitService
 * @param executionContext
 */
class GitHandler @Inject()(gitService: GitService) (implicit val executionContext: ExecutionContext) {
    import GitHandler._
    def handle(org: String): Future[JsArray] = {
        gitService.getRepositories(org)
            .map(fetchContributorsFullData).flatten
            .map(prepareContributorsNecessaryData)
            .map(toJson)
    }

    private def fetchContributorsFullData(repositoriesFullData: Seq[JsArray]): Future[Seq[JsArray]] = {
        Future.sequence(
            repositoriesFullData.flatMap(jsArray => jsArray.\\(fullName)
                .map(_.as[String]).map(fullName => gitService.getContributorsPerRepo(fullName)))
        ).map(_.flatten)
    }

    private def prepareContributorsNecessaryData(contributors: Seq[JsArray]): Seq[(String, Int)] = {
       contributors.flatMap(jsArray => jsArray.\\(login).map(_.as[String])
            .zip(jsArray.\\(contributions).map(_.as[Int])))
            //reduce to contributor -> count
            .groupMapReduce(_._1)(tuple => tuple._2)(_ + _)
            //sortBy number of contributions
            .toSeq.sortWith(_._2 > _._2)
    }

    private def toJson(seq: Seq[(String, Int)]): JsArray = {
        JsArray(seq.map(tuple =>
            JsObject(Seq(name -> JsString(tuple._1), contributions -> JsNumber(tuple._2)))))
    }

}

object GitHandler {
    val fullName = "full_name"
    val login = "login"
    val contributions = "contributions"
    val name = "name"
}
