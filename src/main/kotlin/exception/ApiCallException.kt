package exception

class ApiCallException(override val message : String) : RuntimeException(message)
