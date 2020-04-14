package utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.movie_details_info.recycle_filme_elenco
import java.text.DateFormat
import java.text.Normalizer
import java.text.SimpleDateFormat

/**
 * Created by icaro on 03/09/17.
 */

/**
 * IMAGEVIEW
 */

fun ImageView.setPicasso(stillPath: String?, patten: Int = 4, sucesso: () -> Unit = {}, error: () -> Unit = {}, img_erro: Int = R.drawable.poster_empty): ImageView {
    Picasso.get()
        .load(UtilsApp
            .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, patten)) + stillPath)
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

fun ImageView.setPicassoWithCache(stillPath: String?, patten: Int = 4,
    sucesso: () -> Unit = {},
    error: () -> Unit = {},
    img_erro: Int = R.drawable.poster_empty): ImageView {
    Picasso.get()
        .load(UtilsApp
            .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, patten)) + stillPath)
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

fun ImageView.setPicassoWithCacheAndHolder(stillPath: String?, patten: Int = 4,
    sucesso: () -> Unit = {},
    error: () -> Unit = {},
    img_erro: Int = R.drawable.poster_empty,
    holder: Int): ImageView {
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

fun View.animeRotation() {
    ObjectAnimator
        .ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f, 1.0f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f, 1.0f))
        .apply {
            duration = 1600
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

@Throws(Exception::class)
fun String.parseDate(): String {

    return try {
        val sim = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val data = sim.parse(this)
        DateFormat.getDateInstance(DateFormat.SHORT).format(data)
    } catch (ex: Exception) {
        "N/A"
    }
}

@Throws(Exception::class)
fun String.parseDateShot(): String {
    return try {
        val sim = SimpleDateFormat("yyyy-MM-dd")
        val data = sim.parse(this)
        DateFormat.getDateInstance(DateFormat.SHORT).format(data)
    } catch (ex: Exception) {
        "N/A"
    }
}

@Throws(Exception::class)
fun String.yearDate(): String {
    return this.substring(0, 4)
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

