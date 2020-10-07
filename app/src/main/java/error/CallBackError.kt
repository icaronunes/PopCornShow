package error

interface CallBackError {
    fun tryAgain(): Unit {}
    fun close():Boolean = true
    fun text(): String
    fun hasTry(): Boolean = false
	fun closeFunc()
}
