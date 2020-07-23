package utils.kotterknife

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// Kotterknife implementation
fun <V : View> View.findView(id: Int):
		ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V : View> Activity.findView(id: Int):
		ReadOnlyProperty<Activity, V> =
	required(id, viewFinder)


fun <V : View> Fragment.findView(id: Int):
		ReadOnlyProperty<Fragment, V> =
	required(id, viewFinder)

fun <V : View> Dialog.findView(id: Int):
		ReadOnlyProperty<Dialog, V> = required(id, viewFinder)

fun <V : View> ViewHolder.findView(id: Int):
		ReadOnlyProperty<ViewHolder, V> =
	required(id, viewFinder)

fun <V : View> View.findOptionalView(id: Int):
		ReadOnlyProperty<View, V?> = optional(id, viewFinder)

fun <V : View> Activity.findOptionalView(id: Int):
		ReadOnlyProperty<Activity, V?> =
	optional(id, viewFinder)

fun <V : View> Dialog.findOptionalView(id: Int):
		ReadOnlyProperty<Dialog, V?> = optional(id, viewFinder)

fun <V : View> ViewHolder.findOptionalView(id: Int):
		ReadOnlyProperty<ViewHolder, V?> =
	optional(id, viewFinder)

fun <V : View> View.findViews(vararg ids: Int):
		ReadOnlyProperty<View, List<V>> =
	required(ids, viewFinder)

fun <V : View> Activity.findViews(vararg ids: Int):
		ReadOnlyProperty<Activity, List<V>> =
	required(ids, viewFinder)

fun <V : View> Dialog.findViews(vararg ids: Int):
		ReadOnlyProperty<Dialog, List<V>> =
	required(ids, viewFinder)

fun <V : View> ViewHolder.findViews(vararg ids: Int):
		ReadOnlyProperty<ViewHolder, List<V>> =
	required(ids, viewFinder)

fun <V : View> View.findOptionalViews(vararg ids: Int):
		ReadOnlyProperty<View, List<V>> =
	optional(ids, viewFinder)

fun <V : View> Activity.findOptionalViews(vararg ids: Int):
		ReadOnlyProperty<Activity, List<V>> =
	optional(ids, viewFinder)

fun <V : View> Dialog.findOptionalViews(vararg ids: Int):
		ReadOnlyProperty<Dialog, List<V>> =
	optional(ids, viewFinder)

fun <V : View> ViewHolder.findOptionalViews(vararg ids: Int):
		ReadOnlyProperty<ViewHolder, List<V>> =
	optional(ids, viewFinder)

private val View.viewFinder: View.(Int) -> View?
	get() = { findViewById(it) }
private val Activity.viewFinder: Activity.(Int) -> View?
	get() = { findViewById(it) }
private val Dialog.viewFinder: Dialog.(Int) -> View?
	get() = { findViewById(it) }
private val ViewHolder.viewFinder: ViewHolder.(Int) -> View?
	get() = { itemView.findViewById(it) }
private val Fragment.viewFinder: Fragment.(Int) -> View?
	get() = { view?.findViewById(it) } /// ????

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
	throw IllegalStateException("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?) =
	Lazy { t: T, desc ->
		t.finder(id) as V? ?: viewNotFound(id, desc)
	}

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(id: Int, finder: T.(Int) -> View?) =
	Lazy { t: T, _ -> t.finder(id) as V? }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(ids: IntArray, finder: T.(Int) -> View?) =
	Lazy { t: T, desc ->
		ids.map {
			t.finder(it) as V? ?: viewNotFound(it, desc)
		}
	}

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(ids: IntArray, finder: T.(Int) -> View?) =
	Lazy { t: T, _ ->
		ids.map {
			t.finder(it) as V?
		}.filterNotNull()
	}