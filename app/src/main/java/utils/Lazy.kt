package utils

import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Lazy<T, W>(private val block: (T, KProperty<*>) -> W) : ReadOnlyProperty<T, W>,
	LifecycleObserver {
	private object NOTHING

	private var value: Any? = NOTHING
	private var attachedLifecycle = false

	override fun getValue(thisRef: T, property: KProperty<*>): W {
		addLifecycleOwner(thisRef)
		if (value == NOTHING) {
			value = block(thisRef, property)
		}
		return value as W
	}

	private fun addLifecycleOwner(thisRef: T) {
		if (!attachedLifecycle && thisRef is LifecycleOwner) {
			thisRef.lifecycle.addObserver(this)
			attachedLifecycle = true
		}
	}

	@OnLifecycleEvent(ON_STOP)
	fun destroy() { value = NOTHING }
}