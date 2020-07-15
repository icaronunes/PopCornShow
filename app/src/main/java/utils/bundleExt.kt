package utils

import android.app.Activity
import androidx.fragment.app.Fragment
import java.lang.IllegalArgumentException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val Activity.finder: Activity.(String) -> Any?
	get() = { intent.extras?.get(it) }

private val Fragment.finder: Fragment.(String) -> Any?
	get() = { arguments?.get(it) }


fun <T : Any> Activity.bindBundle(key: String, default: T? = null): ReadOnlyProperty<Activity, T> {
	return if (default == null) { required(key, finder) } else { requiredDefault(key, default, finder) }
}

fun <T: Any> Fragment.bindArgument(key: String, default: T? = null) : ReadOnlyProperty<Fragment, T> {
	return if(default == null) { required(key, finder) } else { requiredDefault(key, default, finder) }
}

fun <T> Activity.bundleOrNull(key: String): ReadOnlyProperty<Activity, T?> = getOrNull(key, finder)
fun <T> Fragment.argumentOrNull(key: String): ReadOnlyProperty<Fragment, T?> = getOrNull(key, finder)

@Suppress("UNCHECKED_CAST")
private fun <T, W: Any?> required(key: String, finder: T.(String) -> Any?) =
	Lazy { type: T, description: KProperty<*> -> type.finder(key) as W ?: bundleNotFound(key, description) }

@Suppress("UNCHECKED_CAST")
private fun <T, W: Any?> requiredDefault(key: String, default: W, finder: T.(String) -> Any?) =
	Lazy { type: T, _: KProperty<*> -> type.finder(key) as W ?: default }

@Suppress("UNCHECKED_CAST")
private fun <T, W: Any?>  getOrNull(key: String, finder: T.(String) -> Any?) = Lazy { type: T, _: KProperty<*> ->
	type.finder(key) as W?
}

private fun bundleNotFound(key:String, description: KProperty<*>): Nothing { throw IllegalArgumentException("Key: $key is not found - ${description.name}")}