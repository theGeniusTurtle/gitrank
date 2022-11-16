package exceptions

case class ForbiddenException(message: String) extends Exception(message)
