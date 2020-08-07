package error

interface CallBackError {
    fun tryAgain(): Unit
    fun close():Boolean
    fun text(): String
    fun hasTry(): Boolean
	fun closeFunc()
}
