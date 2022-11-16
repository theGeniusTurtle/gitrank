package utlis

import org.apache.commons.lang3.StringUtils
import play.api.libs.ws.WSResponse

import scala.annotation.tailrec

/**
 * Helper class for parsing headers from http response
 * Primarily used to determine if and what is the next page in case of response being paginated
 */
object HeaderParser {

    def getOptNextPage(headers: Map[String, scala.collection.Seq[String]]): Option[String] = {
        headers.getOrElse("Link", None) match {
            case head :: Nil =>
                determineNextPage(head)
            case _ => None
        }
    }

    //Sample Link header which contains info about next page
    //<https://api.github.com/organizations/81/repos?page=2>; rel="next", <https://api.github.com/organizations/81/repos?page=11>; rel="last"
    //<https://api.github.com/organizations/81/repos?page=1>; rel="prev", <https://api.github.com/organizations/81/repos?page=3>; rel="next", <https://api.github.com/organizations/81/repos?page=11>; rel="last", <https://api.github.com/organizations/81/repos?page=1>; rel="first"
    private def determineNextPage(links: String): Option[String] = {
        @tailrec
        def findNextRecursively(array: Array[String]): Option[String] = {
            array.headOption match {
                case Some(value) =>
                    val possibleNext = value.split(";")
                    possibleNext.reverse.headOption match {
                        case Some(indicator) if indicator.contains("next") =>
                            possibleNext.headOption match {
                                case Some(nextLink) =>
                                    Some(StringUtils.substringBetween(nextLink, "<", ">"))
                                case _ => findNextRecursively(array.tail)
                            }
                        case Some(_) if array.tail.nonEmpty => findNextRecursively(array.tail)
                        case _ => None
                    }
                case _ => None
            }
        }

        val linksArray = links.split(",")

        findNextRecursively(linksArray)

    }

}
