package exceptions

case class NotFoundException(message: String) extends Exception(message)
