package utils

import configuracao.SettingsActivity
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.Api
import domain.UserTvshow
import domain.tvshow.Tvshow
import kotlinx.coroutines.*

class UtilsKt {

    companion object {

        fun atualizarSerie(context: Context?, serie: Tvshow): UserTvshow {
            val userTvshow = UtilsApp.setUserTvShow(serie)
            var rotina: Job = Job()
            serie.seasons?.forEachIndexed { index, seasonsItem ->
                try {
                    rotina = GlobalScope.launch(Dispatchers.Main) {
                        val season = async(Dispatchers.IO) { Api(context = context!!).getTvSeasons(serie.id!!, seasonsItem?.seasonNumber!!) }.await()
                        val userEp = UtilsApp.setEp2(season)
                        if (userEp != null)
                            userTvshow.seasons!![index].userEps = userEp

                    }
                } catch (ex: Exception) {
                    Toast.makeText(context, context?.getString(R.string.ops), Toast.LENGTH_SHORT).show()
                }
            }
            if (rotina.isActive || rotina.isCompleted) rotina.cancel()
            return userTvshow
        }

        fun getIdiomaEscolhido(context: Context?): String {

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val idioma = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
            return if (idioma) {
                UtilsApp.getLocale()
            } else {
                "en"
            }
        }

        fun getAnuncio(context: Context, quant: Int = 1, listener: (UnifiedNativeAd) -> Unit = {}) {

            val adLoader = AdLoader.Builder(context, "ca-app-pub-7639270198633263/2223415498")
                    //TODO AdMob Cadastrado
                    .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                        // Show the ad.
                        //Retornando Ad para ser usado
                        listener(ad)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(errorCode: Int) {
                            Log.d(this.javaClass.name, errorCode.toString())
                            // Handle the failure by logging, altering the UI, and so on.
                            //	Toast.makeText(context, context.getString(R.string.ops), Toast.LENGTH_LONG).show()
                        }
                    })
                    .withNativeAdOptions(NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_LEFT)
                            .setImageOrientation(NativeAdOptions.ORIENTATION_PORTRAIT)
                            .build())
                    .build()

            adLoader.loadAds(AdRequest.Builder().build(), quant)
        }

    }


}
