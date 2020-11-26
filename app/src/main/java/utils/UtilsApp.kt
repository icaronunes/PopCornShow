package utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build.*
import android.os.Environment
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.palette.graphics.Palette
import com.google.firebase.crashlytics.FirebaseCrashlytics
import configuracao.SettingsActivity
import domain.UserSeasons
import domain.UserTvshow
import domain.busca.ResultsItem
import domain.tvshow.Tvshow
import utils.enums.EnumTypeMedia
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList
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
	val locale: String
		get() = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			Locale.getDefault().toLanguageTag()
		} else {
			Locale.getDefault().language + "-" + Locale.getDefault().country
		}

	fun setUserTvShow(serie: Tvshow): UserTvshow {
		return UserTvshow().apply {
			poster = serie.posterPath
			id = serie.id
			nome = serie.originalName
			numberOfEpisodes = serie.numberOfEpisodes ?: 0
			numberOfSeasons = serie.numberOfSeasons ?: 0
			seasons = setUserSeasson(serie)
		}
	}

	private fun setUserSeasson(serie: Tvshow): MutableList<UserSeasons> {
		val list = ArrayList<UserSeasons>()
		return try {
			for (tvSeason in serie.seasons) {
				val userSeasons = UserSeasons(userEps = mutableListOf())
				userSeasons.id = tvSeason.id
				userSeasons.seasonNumber = tvSeason.seasonNumber
				list.add(userSeasons)
			}
			list
		} catch (e: Exception) {
			list
		}
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
			FirebaseCrashlytics.getInstance().recordException(e)
		}
	}

	fun isNetWorkAvailable(context: Context?): Boolean {
		val conectivtyManager =
			context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		return (conectivtyManager.activeNetworkInfo != null && //Todo - refazer
			conectivtyManager.activeNetworkInfo!!.isAvailable &&
			conectivtyManager.activeNetworkInfo!!.isConnected)
	}

	fun loadPalette(view: ImageView): Int { // Todo Usar ext
		val imageView = view as ImageView
		val drawable = imageView.drawable as? BitmapDrawable
		if (drawable != null) {
			return Palette.Builder(drawable.bitmap).generate().swatches.last().rgb
		}
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
			return when (info.subtype) {
				TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN // api<8 : troque por 11
				-> "fraca" // 2G
				TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, // api<9 : troque por 14
				TelephonyManager.NETWORK_TYPE_EHRPD, // api<11 : troque por 12
				TelephonyManager.NETWORK_TYPE_HSPAP // api<13 : troque por 15
				-> "fraca" // 3G
				TelephonyManager.NETWORK_TYPE_LTE // api<11 : troque por 13
				-> "forte" // 4G
				else -> "?"
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
		return if (VERSION.SDK_INT >= VERSION_CODES.N) {
			FileProvider.getUriForFile(context, context.packageName + ".provider", file)
		} else {
			Uri.fromFile(file)
		}
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
			Log.d("UtilsApp", e.message ?: "")
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
			if (item.posterPath != null && !item.posterPath.isEmpty() && !item.posterPath.equals(
					"",
					ignoreCase = true
				)
			)
				saveImagemSearch(context, item.posterPath)
		} else if (item.mediaType.equals(EnumTypeMedia.MOVIE.type, ignoreCase = true)) {
			if (item.posterPath != null && !item.posterPath.isEmpty() && !item.posterPath.equals(
					"",
					ignoreCase = true
				)
			)
				saveImagemSearch(context, item.posterPath)
		} else if (item.mediaType.equals(EnumTypeMedia.PERSON.type, ignoreCase = true)) {
			if (item.profile_path != null && !item.profile_path.isEmpty() && !item.profile_path.equals(
					"",
					ignoreCase = true
				)
			)
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
