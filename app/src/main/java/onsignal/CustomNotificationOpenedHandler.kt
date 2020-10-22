package onsignal

import lista.ListGenericActivity
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import applicaton.PopCornApplication
import com.onesignal.OSNotificationAction.ActionType.*
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal.*
import filme.activity.MovieDetailsActivity
import lista.movie.activity.MoviesActivity
import lista.tvshow.activity.TvShowsActivity
import main.MainActivity
import org.json.JSONException
import org.json.JSONObject
import pessoa.activity.FotoPersonActivity
import produtora.activity.ProductionActivity
import similares.SimilaresActivity
import site.Site
import temporada.SeasonActivity
import trailer.TrailerActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.Constant.COLOR
import utils.Constant.COLOR_TOP
import utils.Constant.ID
import utils.Constant.LISTA_ID
import utils.Constant.NAME
import utils.Constant.NAV_DRAW_ESCOLIDO
import utils.Constant.NOME_PERSON
import utils.Constant.POSICAO
import utils.Constant.Signal.FOTOPERSON
import utils.Constant.Signal.LISTGENERIC
import utils.Constant.Signal.MOVIE
import utils.Constant.Signal.MOVIESLIST
import utils.Constant.Signal.PRODUTORA
import utils.Constant.Signal.SIMILARES
import utils.Constant.Signal.SITE
import utils.Constant.Signal.TEMPORADA
import utils.Constant.Signal.TRAILER
import utils.Constant.Signal.TVSHOW
import utils.Constant.Signal.TVSHOWLIST
import utils.Constant.Signal.VIDEO
import utils.Constant.TEMPORADA_ID
import utils.Constant.TVSHOW_ID

/**
 * Created by icaro on 16/10/16.
 */
class CustomNotificationOpenedHandler : NotificationOpenedHandler {
	private lateinit var context: Context
	private var jsonData: JSONObject? = null

	// This fires when a notification is opened by tapping on it.
	override fun notificationOpened(result: OSNotificationOpenResult) {
		context = PopCornApplication.getInstance().baseContext
		jsonData = result.notification.payload.additionalData
		val actionType = result.action.type
		if (actionType == ActionTaken) {
			if ("yes" == result.action.actionID) {
				return
			}
			if ("no" == result.action.actionID) {
				//
				return
			}
			if ("talvez" == result.action.actionID) {
				return
			}
		}
		if (jsonData != null) {
			try {
				val jsonObject: JSONObject = jsonData as JSONObject

				when (jsonObject.getString("action")) {
                    MOVIE -> {
                        callMovieDetails(context, jsonObject)
                    }
                    TVSHOW -> {
                        callTvShow(context, jsonObject)
                    }
                    MOVIESLIST -> {
                        callMoviesList(context, jsonObject)
                    }
                    LISTGENERIC -> {
                        callGenericList(context, jsonObject)
                    }
                    TRAILER, VIDEO -> {
                        callTrailer(context, jsonObject)
                    }
                    SITE -> {
                        callSite(context, jsonObject)
                    }
                    PRODUTORA -> {
                        callProdutora(context, jsonObject)
                    }
                    SIMILARES -> {
                        callSimilares(context, jsonObject)
                    }
                    FOTOPERSON -> {
                        callPersonPhoto(context, jsonObject)
                    }
                    TVSHOWLIST -> {
                        callTvshowList(context, jsonObject)
                    }
                    TEMPORADA -> {
                        callSeason(context, jsonObject)
                    }
				}
			} catch (e: JSONException) {
				e.printStackTrace()
				callMain()
			}
		} else {
			callMain()
		}
	}

	private fun callMain() {
		val intent = Intent(context, MainActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
		context.startActivity(intent)
	}

	private fun callSeason(context: Context, jsonObject: JSONObject) { //todo feito
		if (jsonObject.has(TEMPORADA_ID) && jsonObject.has(TVSHOW_ID)) {
			val intent = Intent(context, SeasonActivity::class.java).apply {
				putExtra(TVSHOW_ID, jsonObject.getInt(TVSHOW_ID))
				putExtra(TEMPORADA_ID, jsonObject.getInt(TEMPORADA_ID))
				putExtra(NAME, jsonObject.getString(NAME))
				putExtra(COLOR_TOP, jsonObject.getString(COLOR_TOP))
			}
			TaskStackBuilder.create(context).apply {
				addParentStack(SeasonActivity::class.java)
				addNextIntent(intent)
				startActivities()
			}
		} else {
			callMain()
		}
	}

	private fun callTvshowList(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, TvShowsActivity::class.java)
		if (jsonObject.has(NAV_DRAW_ESCOLIDO)) {
			// só funciona para NO CINEM
			intent.putExtra(NAV_DRAW_ESCOLIDO, jsonObject.getString(NAV_DRAW_ESCOLIDO));
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(TvShowsActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callPersonPhoto(context: Context, jsonObject: JSONObject) { // Quebrado
		val intent = Intent(context, FotoPersonActivity::class.java)
		if (jsonObject.has(ID)) {
			intent.putExtra(Constant.PERSON_ID, jsonObject.getInt(ID))
			if (jsonObject.has(NOME_PERSON)) intent.putExtra(NOME_PERSON,
                jsonObject.getString(NOME_PERSON))
			if (jsonObject.has(POSICAO)) intent.putExtra(POSICAO, jsonObject.getInt(POSICAO))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(FotoPersonActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callSimilares(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, SimilaresActivity::class.java)
		if (jsonObject.has("nome")) intent.putExtra(Constant.NOME_FILME,
            jsonObject.getString("nome"))
		if (jsonObject.has("id")) {
			intent.putExtra(Constant.FILME_ID, jsonObject.getInt("id"))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(SimilaresActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callProdutora(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, ProductionActivity::class.java)
		if (jsonObject.has("id")) {
			intent.putExtra(Constant.ID, jsonObject.getInt("id"))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(ProductionActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callSite(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, Site::class.java)
		if (jsonObject.has("url")) {
			intent.putExtra(Constant.SITE, jsonObject.getString("url"))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(Site::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callTrailer(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, TrailerActivity::class.java)
		if (jsonObject.has("sinopse")) intent.putExtra(Constant.SINOPSE,
            jsonObject.getString("sinopse"))
		if (jsonObject.has("youtube_key")) {
			intent.putExtra(Constant.YOU_TUBE_KEY, jsonObject.getString("youtube_key"))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(TrailerActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callGenericList(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, ListGenericActivity::class.java)
		if (jsonObject.has(LISTA_ID)) {
			intent.putExtra(LISTA_ID, jsonObject.getString(LISTA_ID))
			if (jsonObject.has(NAME)) intent.putExtra(Constant.LISTA_NOME,
                jsonObject.getString(NAME))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(ListGenericActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callMoviesList(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, MoviesActivity::class.java)
		if (jsonObject.has(NAV_DRAW_ESCOLIDO)) {
			intent.putExtra(NAV_DRAW_ESCOLIDO, jsonObject.getString(NAV_DRAW_ESCOLIDO))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(MainActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callTvShow(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, TvShowActivity::class.java)
		if (jsonObject.has(ID)) {
			intent.putExtra(TVSHOW_ID, jsonObject.getInt(ID))
			if (jsonObject.has(COLOR)) intent.putExtra(COLOR, jsonObject.getInt(COLOR))
			if (jsonObject.has(NAME)) intent.putExtra(Constant.NOME_TVSHOW,
                jsonObject.getString(NAME))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(TvShowActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}

	private fun callMovieDetails(context: Context, jsonObject: JSONObject) {
		val intent = Intent(context, MovieDetailsActivity::class.java)
		if (jsonObject.has(ID)) {
			intent.putExtra(Constant.FILME_ID, jsonObject.getInt(ID))
			if (jsonObject.has(COLOR)) intent.putExtra(COLOR, jsonObject.getInt(COLOR))
			val stackBuilder = TaskStackBuilder.create(context)
			stackBuilder.addParentStack(MovieDetailsActivity::class.java)
			stackBuilder.addNextIntent(intent)
			stackBuilder.startActivities()
		} else {
			callMain()
		}
	}
}