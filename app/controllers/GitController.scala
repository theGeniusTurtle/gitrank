package controllers

import exceptions.{ForbiddenException, NotFoundException}
import handler.GitHandler
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Entry point for incoming request to the /org/:org/contributors endpoint
 * @param cc
 * @param gitHandler
 * @param executionContext
 */
class GitController @Inject() (cc: ControllerComponents, gitHandler: GitHandler)
                              (implicit val executionContext: ExecutionContext) extends AbstractController(cc){

    def getContributors(org: String): Action[AnyContent] = {
        Action.async {
            val data = gitHandler.handle(org)
            data.map{
                case jsonArray => Ok(Json.prettyPrint(jsonArray))
            } recoverWith {
                case ForbiddenException(message) =>
                    Future.successful(Forbidden(JsObject(Seq("message" -> JsString(message)))))
                case NotFoundException(message) =>
                    Future.successful(NotFound(JsObject(Seq("message" -> JsString(message)))))
                case t: Throwable =>
                    Future.successful(InternalServerError(
                        JsObject(Seq("message" -> JsString(Option(t.getMessage)
                            .getOrElse("Something went wrong without error message"))))
))
            }
        }
    }

}
