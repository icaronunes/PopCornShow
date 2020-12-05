
private val ZERO = 0
private val EMPTY_STRING = ""

infix fun <T, R> T.ifValid(function: () -> R?) = if (hasValue) function.invoke() else null
infix fun <T, R> T.ifNotValid(function: () -> R?) = if (!hasValue) function.invoke() else null

fun <T> forEachArg(vararg any: T, function: T.() -> Unit) = any.forEach { function.invoke(it) }

infix fun <T> T.otherWise(other: T) = if (hasValue) this else other

infix fun <T> T?.safely(function: (T) -> Unit) {
    if (hasValue && this != null) function.invoke(this)
}

val <T> T.hasValue
    get() = when (this) {
        null -> false
        false -> false
        ZERO -> false
        EMPTY_STRING -> false
        ZERO.toString() -> false
        ZERO.toLong() -> false
        ZERO.toDouble() -> false
        ZERO.toFloat() -> false
        arrayListOf<T>() -> false
        listOf<T>() -> false
        {} -> false
        else -> true
    }