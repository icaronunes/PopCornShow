package utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.palette.graphics.Palette
import com.crashlytics.android.Crashlytics
import configuracao.SettingsActivity
import domain.FilmeService
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import domain.UserTvshow
import domain.busca.ResultsItem
import domain.tvshow.ExternalIds
import domain.tvshow.Tvshow
import info.movito.themoviedbapi.model.config.Timezone
import info.movito.themoviedbapi.model.tv.TvSeason
import utils.enums.EnumTypeMedia
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by icaro on 24/06/16.
 */

object UtilsApp {

    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    /* Checks if external storage is available to at least read */
    val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    val locale: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.getDefault().toLanguageTag()
        } else {
            Locale.getDefault().language + "-" + Locale.getDefault().country
        }

    val timezone: Timezone
        get() {
            for (timezone in FilmeService.getTimeZone()) {
                if (timezone.country == Locale.getDefault().country) {
                    return timezone
                }
            }
            return Timezone("US", "US")
        }

    fun setUserTvShow(serie: Tvshow): UserTvshow {
        val userTvshow = UserTvshow()
        userTvshow.poster = serie.posterPath
        userTvshow.id = serie.id ?: -1
        userTvshow.nome = serie.originalName
        // userTvshow.setExternalIds(valoresExternalIds(serie.getExternal_ids()));
        userTvshow.numberOfEpisodes = serie.numberOfEpisodes ?: 0
        userTvshow.numberOfSeasons = serie.numberOfSeasons ?: 0
        userTvshow.seasons = setUserSeasson(serie)
        return userTvshow
    }

    private fun valoresExternalIds(external_ids: ExternalIds): domain.ExternalIds {
        val ext = domain.ExternalIds()
        ext.freebaseMid = external_ids.freebaseMid
        ext.imdbId = external_ids.imdbId
        ext.tvdbId = external_ids.tvdbId
        ext.tvrageId = external_ids.tvrageId

        return ext
    }

    private fun setUserSeasson(serie: Tvshow): MutableList<UserSeasons> {
        val list = ArrayList<UserSeasons>()
        return try {
            for (tvSeason in serie.seasons!!) {
                val userSeasons = UserSeasons()
                userSeasons.id = tvSeason?.id!!
                userSeasons.seasonNumber = tvSeason.seasonNumber!!
                list.add(userSeasons)
            }
            list
        } catch (e: Exception) {
            list
        }
    }

    fun setEp(tvSeason: TvSeason): List<UserEp> {
        val eps = ArrayList<UserEp>()
        for (tvEpisode in tvSeason.episodes) {
            val userEp = UserEp()
            userEp.episodeNumber = tvEpisode.episodeNumber
            userEp.id = tvEpisode.id
            userEp.seasonNumber = tvEpisode.seasonNumber
            eps.add(userEp)
        }

        return eps
    }

    fun setEp2(tvSeason: TvSeasons): List<UserEp>? {
        return try {
            val eps = mutableListOf<UserEp>()
            for (tvEpisode in tvSeason.episodes!!) {
                val userEp = UserEp()
                userEp.episodeNumber = tvEpisode?.episodeNumber!!
                userEp.id = tvEpisode.id!!
                userEp.seasonNumber = tvEpisode.seasonNumber
                eps.add(userEp)
            }
            eps
        } catch (e: Exception) {
            mutableListOf<UserEp>()
        }
    }

    fun verificaLancamento(air_date: Date?): Boolean {
        val data: Boolean
        // Arrumar. Ta esquisito.
        if (air_date == null) return false
        val myDate = Calendar.getInstance().time
        if (air_date.before(myDate)) {
            data = true
        } else
            data = !air_date.after(myDate)
        return data
    }

    fun verificaDataProximaLancamento(air_date: Date?): Boolean {

        if (air_date == null) return false
        val calendar = Calendar.getInstance()
        calendar.time = air_date
        val hoje = Calendar.getInstance()
        hoje.time = Calendar.getInstance().time

        return if (calendar.after(hoje)) {
            false
        } else {
            if (calendar.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)) {
                calendar.get(Calendar.WEEK_OF_YEAR) == hoje.get(Calendar.WEEK_OF_YEAR)
            } else false
        }
    }

    fun verificaDataProximaLancamentoDistante(air_date: Date): Boolean {
        val no_ar = Calendar.getInstance()
        no_ar.time = air_date
        val hoje = Calendar.getInstance()
        hoje.time = Calendar.getInstance().time

        return if (no_ar.after(hoje)) {
            false
        } else {
            if (no_ar.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)) {
                no_ar.get(Calendar.DAY_OF_YEAR) < hoje.get(Calendar.DAY_OF_YEAR) + 15
            } else false
        }
    }

    fun writeBytes(file: File, bytes: ByteArray) {
        try {
            val stream = FileOutputStream(file)
            stream.write(bytes)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            // Log.e(TAG, e.getMessage(), e);
            Crashlytics.logException(e)
        }
    }

    fun salvaImagemMemoriaCache(context: Context, imageView: ImageView?, endereco: String): File {
        // Usar metodo do activity.BaseActivity
        val file = context.externalCacheDir

        if (!file!!.exists()) {
            file.mkdir()
            //  Log.e("salvarArqNaMemoriaIn", "Directory created");
        }
        val dir = File(file, endereco)
        if (imageView != null) {
            val drawable = imageView.drawable as BitmapDrawable
            if (drawable != null) {
                val bitmap = drawable.bitmap
                UtilsApp.writeBitmap(dir, bitmap)
            }
        }
        return dir
    }

    fun aguardarImagemPesquisa(context: Context, bitmap: Bitmap?, endereco: String): File {

        val file = context.externalCacheDir

        if (!file!!.exists()) {
            file.mkdir()
            //  Log.e("salvarArqNaMemoriaIn", "Directory created");
        }
        val dir = File(file, endereco)

        if (bitmap != null) {
            UtilsApp.writeBitmap(dir, bitmap)
        }

        return dir
    }

    fun writeBitmap(file: File, bitmap: Bitmap) {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
        } catch (e: IOException) {
            Crashlytics.logException(e)
        }
    }

    fun isNetWorkAvailable(context: Context?): Boolean {
        val conectivtyManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (conectivtyManager.activeNetworkInfo != null &&
            conectivtyManager.activeNetworkInfo.isAvailable &&
            conectivtyManager.activeNetworkInfo.isConnected)
    }

    fun loadPalette(view: ImageView): Int {

        val imageView = view as ImageView
        val drawable = imageView.drawable as? BitmapDrawable
        if (drawable != null) {
            val bitmap = drawable.bitmap
            val builder = Palette.Builder(bitmap)
            val palette = builder.generate()
            for (swatch in palette.swatches) {
                return swatch.rgb
            }
        } //Todo verificar o uso de swatch para fazer textos
        return 0
    }

    fun getNetworkClass(context: Context?): String {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo

        if (info == null || !info.isConnected)
            return "-" // sem conexão
        if (info.type == ConnectivityManager.TYPE_WIFI)
            return "forte" // WIFI
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            val networkType = info.subtype
            when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN // api<8 : troque por 11
                -> return "fraca" // 2G
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, // api<9 : troque por 14
                TelephonyManager.NETWORK_TYPE_EHRPD, // api<11 : troque por 12
                TelephonyManager.NETWORK_TYPE_HSPAP // api<13 : troque por 15
                -> return "fraca" // 3G
                TelephonyManager.NETWORK_TYPE_LTE // api<11 : troque por 13
                -> return "forte" // 4G
                else -> return "?"
            }
        }
        return "?"
    }

    fun getTamanhoDaImagem(context: Context?, padrao: Int): Int {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val ativo = sharedPref.getBoolean(SettingsActivity.PREF_SAVE_CONEXAO, true)

        if (!ativo) {
            return padrao
        } else {
            val conexao = getNetworkClass(context)

            return if (conexao == "forte") {
                padrao
            } else {
                if (padrao >= 2) {
                    padrao - 1
                } else {
                    1
                }
            }
        }
    }

    fun getUriDownloadImage(context: Context, file: File): Uri {
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        } else {
            uri = Uri.fromFile(file)
        }

        return uri
    }

    fun saveImagemSearch(context: Context, enderecoImagem: String?) {
        var input: InputStream? = null
        try {
            val url = URL(getBaseUrlImagem(1)!! + enderecoImagem!!)
            input = url.openStream()

            val storagePath = context.externalCacheDir!!.toString() + "/" + enderecoImagem
            val output = FileOutputStream(File(storagePath))

            val buffer = ByteArray(10000000)
            var bytesRead = 0
            while ((input!!.read(buffer, 0, buffer.size)) >= 0) {
                output.write(buffer, 0, (input.read(buffer, 0, buffer.size)))
            }
        } catch (e: Exception) {
            Log.d("UtilsApp", e.message)
        } finally {
            try {
                input?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun gravarImg(context: Context, item: ResultsItem) {

        if (item.mediaType!!.equals(EnumTypeMedia.TV.type, ignoreCase = true)) {
            if (item.posterPath != null && !item.posterPath.isEmpty() && !item.posterPath.equals("", ignoreCase = true))
                saveImagemSearch(context, item.posterPath)
        } else if (item.mediaType.equals(EnumTypeMedia.MOVIE.type, ignoreCase = true)) {
            if (item.posterPath != null && !item.posterPath.isEmpty() && !item.posterPath.equals("", ignoreCase = true))
                saveImagemSearch(context, item.posterPath)
        } else if (item.mediaType.equals(EnumTypeMedia.PERSON.type, ignoreCase = true)) {
            if (item.profile_path != null && !item.profile_path.isEmpty() && !item.profile_path.equals("", ignoreCase = true))
                saveImagemSearch(context, item.profile_path)
        }
    }

    fun getBaseUrlImagem(tamanho: Int): String? {

        when (tamanho) {
            1 -> {
                return "http://image.tmdb.org/t/p/w92/"
            }
            2 -> {
                return "http://image.tmdb.org/t/p/w154/"
            }
            3 -> {
                return "http://image.tmdb.org/t/p/w185/"
            }
            4 -> {
                return "http://image.tmdb.org/t/p/w342/"
            }
            5 -> {
                return "http://image.tmdb.org/t/p/w500/"
            }
            6 -> {
                return "http://image.tmdb.org/t/p/w780/"
            }
            7 -> {
                return "http://image.tmdb.org/t/p/original/"
            }
            else -> return "http://image.tmdb.org/t/p/w92/"
        }
    }
}
