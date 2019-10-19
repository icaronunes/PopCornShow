package utils

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.Normalizer
import java.text.SimpleDateFormat

/**
 * Created by icaro on 03/09/17.
 */

/**
 * IMAGEVIEW
 */

fun ImageView.setPicasso(stillPath: String?, patten: Int = 4, sucesso: () -> Unit = {}, error: () -> Unit = {}, img_erro: Int = R.drawable.poster_empty) {
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
}

fun ImageView.setPicassoWithCache(stillPath: String?, patten: Int = 4, sucesso: () -> Unit = {}, error: () -> Unit = {}, img_erro: Int = R.drawable.poster_empty) {
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
