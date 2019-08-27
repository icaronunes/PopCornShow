package utils

import android.widget.ImageView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

/**
 * Created by icaro on 03/09/17.
 */

fun ImageView.setPicasso(stillPath: String?, patten: Int = 4,  sucesso: () -> Unit = {}, error: () -> Unit = {}, img_erro: Int = R.drawable.poster_empty) {
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

fun ImageView.setPicassoWithCache(stillPath: String?, patten: Int = 4,  sucesso: () -> Unit = {}, error: () -> Unit = {}, img_erro: Int = R.drawable.poster_empty) {
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
