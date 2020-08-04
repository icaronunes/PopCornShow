package utils

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.reelgood.movie.Availability
import domain.tvshow.Tvshow
import java.text.DateFormat
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by icaro on 03/09/17.
 */

/**
 * IMAGEVIEW
 */

fun ImageView.setPicasso(
	stillPath: String?,
	patten: Int = 4,
	sucesso: () -> Unit = {},
	error: () -> Unit = {},
	img_erro: Int = R.drawable.poster_empty
): ImageView {
	Picasso.get()
		.load(
			UtilsApp
				.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, patten)) + stillPath
		)
		.error(img_erro)
		.memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
		.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
		.into(this, object : Callback {
			override fun onSuccess() {
				sucesso()
			}

			override fun onError(e: java.lang.Exception?) {
				error()
			}
		})
	return this
}

fun ImageView.setPicassoWithCache(
	stillPath: String?, patten: Int = 4,
	sucesso: () -> Unit = {},
	error: () -> Unit = {},
	img_erro: Int = R.drawable.poster_empty
): ImageView {
	Picasso.get()
		.load(
			UtilsApp
				.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, patten)) + stillPath
		)
		.error(img_erro)
		.into(this, object : Callback {
			override fun onSuccess() {
				sucesso()
			}

			override fun onError(e: java.lang.Exception?) {
				error()
			}
		})
	return this
}

fun ImageView.setPicassoWithCacheAndHolder(
	stillPath: String?, patten: Int = 4,
	sucesso: () -> Unit = {},
	error: () -> Unit = {},
	img_erro: Int = R.drawable.poster_empty,
	holder: Int
): ImageView {
	Picasso.get()
		.load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, patten)) + stillPath)
		.placeholder(holder)
		.error(img_erro)
		.into(this, object : Callback {
			override fun onSuccess() {
				sucesso()
			}

			override fun onError(e: java.lang.Exception?) {
				error()
			}
		})
	return this
}

fun ImageView.loadPallet(): Int? {
	val color = (drawable as? BitmapDrawable)?.run {
		Palette.Builder(this.bitmap).generate().swatches.first().rgb
	}
	return color ?: 0
}

fun loadPalette(view: ImageView): Int { // Todo Usar ext

	val imageView = view as ImageView
	val drawable = imageView.drawable as? BitmapDrawable
	if (drawable != null) {
		return Palette.Builder(drawable.bitmap).generate().swatches.last().rgb
	}
	return 0
}

/**
 * ACTIVITY
 */

fun Activity.makeToast(restText: Int, time: Int = Toast.LENGTH_SHORT) {
	this.makeToast(this.getString(restText), time)
}

fun Activity.makeToast(text: String?, time: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(this, text, time).show()
}

/**
 * VIEW
 */

fun View.gone() {
	this.visibility = View.GONE
}

fun View.visible() {
	this.visibility = View.VISIBLE
}

fun View.invisible() {
	this.visibility = View.INVISIBLE
}

fun View.animeRotation(
	end: () -> Unit = {},
	cancel: () -> Unit = {},
	start: () -> Unit = {},
	repeat: () -> Unit = {}
) {
	ObjectAnimator
		.ofPropertyValuesHolder(
			this,
			PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.2f),
			PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.2f),
			PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f, 1.0f),
			PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f, 1.0f)
		)
		.apply {
			duration = 790
			addListener(object : AnimatorListener {
				override fun onAnimationRepeat(animation: Animator?) {
					repeat()
				}

				override fun onAnimationEnd(animation: Animator?) {
					end()
				}

				override fun onAnimationCancel(animation: Animator?) {
					cancel()
				}

				override fun onAnimationStart(animation: Animator?) {
					start()
				}
			})
		}.start()
}

/**
 * Any
 */

fun Any.putString(cxt: Context): String = when (this) {
	is String -> this
	is Int -> cxt.getString(this)
	else -> {
		require(false) { "Need R.string.id or string" }
		""
	}
}

/**
 * STRING
 */

fun String.removerAcentos(): String {
	this.replace(".", "")
	this.replace(":", "")
	this.replace("/", "")
	this.replace(";", "")
	return Normalizer.normalize(this, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "")
}

fun String.parseDate(): String {

	return try {
		val sim = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		val data = sim.parse(this)
		DateFormat.getDateInstance(DateFormat.SHORT).format(data)
	} catch (ex: Exception) {
		"-/-"
	}
}

fun String.parseDateShot(): String {
	return try {
		val sim = SimpleDateFormat("yyyy-MM-dd")
		val data = sim.parse(this)
		DateFormat.getDateInstance(DateFormat.SHORT).format(data)
	} catch (ex: Exception) {
		"-/-"
	}
}

fun verifyLaunch(air_date: Date?): Boolean {
	if (air_date == null) return false
	val now = Calendar.getInstance().time
	return if (air_date.before(now)) {
		true
	} else !air_date.after(now)
}

fun String.getDate(format: String = "yyyy-MM-dd"): Date? {
	return try {
		let {
			val sdf = SimpleDateFormat(format, Locale.getDefault())
			sdf.parse(it)
		}
	} catch (e: ParseException) {
		e.printStackTrace()
		null
	}
}

fun String.released(): Boolean {
	val data = getDate()
	return verifyLaunch(data)
}

@Throws(Exception::class)
fun String.yearDate(): String {
	return this.substring(0, 4)
}

fun String.periodLaunch(): Boolean {
	val oneWeekBack = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
	return getDate() != null && getDate()!!.after(oneWeekBack) //valida data
}

fun String.getNameTypeReel(): String {
	return replace(":", "-")
		.replace(" ", "-")
		.replace("&", "and")
		.replace(".", "")
		.replace("é", "e")
		.replace("ẽ", "e")
		.replace("è", "e")
		.replace("ë", "e")
		.replace("ç", "c")
		.replace("â", "a")
		.replace("ã", "a")
		.replace("á", "a")
		.replace("à", "a")
		.replace("ä", "a")
		.replace("ä", "a")
		.replace("'", "")
		.replace("\"", "")
		.replace("´", "")
		.replace("~", "")
		.replace("^", "")
		.replace("---", "-")
		.replace("----", "-")
		.replace("--", "-")
		.toLowerCase()
}

fun String?.notNullOrEmpty() = !this.isNullOrEmpty()

fun <T> Gson.fromJsonWithLog(json: String?, classOfT: Class<T>): T {
	return this.fromJson<T>(json, classOfT).apply {
		this.toString().log(classOfT.name)
	}
}

fun String.log(tag: String) = Log.i(tag, this)

fun Tvshow.createIdReal() = createIdReal(this.originalName ?: "", this.firstAirDate ?: "")

private fun createIdReal(originalName: String, data: String) =
	"${originalName.getNameTypeReel()}-${data.yearDate()}"

/**
 * RECYCLER
 */

fun RecyclerView.setScrollInvisibleFloatMenu(floatButton: FloatingActionMenu) {
	addOnScrollListener(object : RecyclerView.OnScrollListener() {

		override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
			when (newState) {
				RecyclerView.SCROLL_STATE_IDLE -> floatButton.visible()
				RecyclerView.SCROLL_STATE_DRAGGING -> floatButton.invisible()
				RecyclerView.SCROLL_STATE_SETTLING -> floatButton.invisible()
			}
		}
	})
}

fun RecyclerView.patternRecyler(horizont: Boolean = true): RecyclerView {
	val typeOrient = if (horizont) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL
	layoutManager = LinearLayoutManager(context, typeOrient, false)
	itemAnimator = DefaultItemAnimator()
	setHasFixedSize(true)
	return this
}

fun RecyclerView.patternRecyclerGrid(quant: Int = 2): RecyclerView {
	layoutManager = GridLayoutManager(context, quant)
	setHasFixedSize(true)
	itemAnimator = DefaultItemAnimator()
	return this
}

fun RecyclerView.minHeight(): RecyclerView {
	layoutParams.height = 1
	importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
	isFocusable = false
	return this
}

/**
 * AVAILABILITY
 */
fun Availability.getPricePurchase(): String {
	val less = purchaseCostSd?.let { purchaseCostHd?.coerceAtMost(it) }
	val biggest = purchaseCostSd?.let { purchaseCostHd?.coerceAtLeast(it) }
	if (less == biggest) return less.toString()
	return "${if (less != 0.0) less.toString() else "--"} - ${if (biggest != 0.0) biggest.toString() else "--"}"
}

fun Availability.getPriceRental(): String {
	val less = rentalCostSd?.let { rentalCostHd?.coerceAtMost(it) }
	val biggest = rentalCostSd?.let { rentalCostHd?.coerceAtLeast(it) }
	if (less == biggest) return less.toString()
	return "${if (less != 0.0) less.toString() else "--"} - ${if (biggest != 0.0) biggest.toString() else "--"}"
}

/**
 * BottomSheetBehavior
 */
fun BottomSheetBehavior<View>.setAnimation(container: View, viewHeight: View) {

	ValueAnimator.ofInt(container.measuredHeight, viewHeight.marginTop).apply {
		addUpdateListener {
			peekHeight = it.animatedValue as Int
		}
		duration = 500
	}.start()

	container.post {
		val newLayoutParams = (container.layoutParams as? MarginLayoutParams)
		newLayoutParams?.setMargins(0, 0, 0, viewHeight.marginTop + 1)
		container.layoutParams = newLayoutParams
	}
}

/**
 * MutableList
 */
fun <T> MutableList<T>.replaceItemList(item: T, predicate: (T) -> Boolean): MutableList<T> {
	return this.map {
		if (predicate(it)) item else it
	}.toMutableList()
}

fun <T> Any.listSize(item: T, size: Int): ArrayList<T> {
	return if (size <= 0) {
		arrayListOf()
	} else {
		arrayListOf<T>().apply {
			for (i in 0 until size) {
				add(item)
			}
		}
	}
}



