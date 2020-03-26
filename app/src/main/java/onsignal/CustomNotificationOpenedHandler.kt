package onsignal

import activity.ListaGenericaActivity
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import applicaton.PopCornApplication
import com.onesignal.OSNotificationAction.ActionType.ActionTaken
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal.NotificationOpenedHandler
import elenco.ElencoActivity
import filme.activity.MovieDetailsActivity
import listafilmes.activity.MoviesActivity
import listaserie.activity.TvShowsActivity
import main.MainActivity
import org.json.JSONException
import org.json.JSONObject
import pessoa.activity.FotoPersonActivity
import pessoa.activity.PersonActivity
import producao.CrewsActivity
import produtora.activity.ProdutoraActivity
import similares.SimilaresActivity
import site.Site
import temporada.TemporadaActivity
import trailer.TrailerActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.Constantes.COLOR_TOP
import utils.Constantes.ID
import utils.Constantes.NOME
import utils.Constantes.NOME_PERSON
import utils.Constantes.POSICAO
import utils.Constantes.Signal.CREWS
import utils.Constantes.Signal.ELENCO
import utils.Constantes.Signal.FOTOPERSON
import utils.Constantes.Signal.LISTGENERIC
import utils.Constantes.Signal.MOVIE
import utils.Constantes.Signal.MOVIESLIST
import utils.Constantes.Signal.PRODUTORA
import utils.Constantes.Signal.SIMILARES
import utils.Constantes.Signal.SITE
import utils.Constantes.Signal.TEMPORADA
import utils.Constantes.Signal.TRAILER
import utils.Constantes.Signal.TVSHOW
import utils.Constantes.Signal.TVSHOWLIST
import utils.Constantes.TEMPORADA_ID
import utils.Constantes.TVSHOW_ID

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

                when (jsonObject["action"] as String) {
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
                    TRAILER -> {
                        callTrailer(context, jsonObject)
                    }
                    ELENCO -> {
                        callElenco(context, jsonObject)
                    }
                    CREWS -> {
                        callCrews(context, jsonObject)
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
                        callTvshowList(context)
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
            val intent = Intent(context, TemporadaActivity::class.java).apply {
                putExtra(TVSHOW_ID, jsonObject.getInt(TVSHOW_ID))
                putExtra(TEMPORADA_ID, jsonObject.getInt(TEMPORADA_ID))
                putExtra(NOME, jsonObject.getString(NOME))
                putExtra(COLOR_TOP, jsonObject.getString(COLOR_TOP))
            }
            TaskStackBuilder.create(context).apply {
                addParentStack(TemporadaActivity::class.java)
                addNextIntent(intent)
                startActivities()
            }
        } else {
            callMain()
        }
    }

    private fun callTvshowList(context: Context) {
        val intent = Intent(context, TvShowsActivity::class.java)

        // if (object.has("aba")) {    só funciona para NO CINEM  // intent.putExtra(Constantes.ABA, object.getInt("id"));
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(TvShowsActivity::class.java)
        stackBuilder.addNextIntent(intent)
        stackBuilder.startActivities()
    }

    private fun callPersonPhoto(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, FotoPersonActivity::class.java)
        if (jsonObject.has(NOME_PERSON)) intent.putExtra(NOME_PERSON, jsonObject.getString("nome"))
        if (jsonObject.has(POSICAO)) intent.putExtra(POSICAO, jsonObject.getInt("position"))
        if (jsonObject.has(ID)) {
            intent.putExtra(Constantes.PERSON_ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(FotoPersonActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callSimilares(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, SimilaresActivity::class.java)
        if (jsonObject.has("nome")) intent.putExtra(Constantes.NOME_FILME, jsonObject.getString("nome"))
        if (jsonObject.has("id")) {
            intent.putExtra(Constantes.FILME_ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(SimilaresActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callProdutora(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, ProdutoraActivity::class.java)
        if (jsonObject.has("id")) {
            intent.putExtra(Constantes.PRODUTORA_ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(ProdutoraActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callSite(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, Site::class.java)
        if (jsonObject.has("url")) {
            intent.putExtra(Constantes.SITE, jsonObject.getString("url"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(Site::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callCrews(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, CrewsActivity::class.java)
        if (jsonObject.has("nome")) intent.putExtra(NOME, jsonObject.getString("nome"))
        if (jsonObject.has("mediatype")) intent.putExtra(Constantes.MEDIATYPE, jsonObject.getString("mediatype"))
        if (jsonObject.has("tvseason")) intent.putExtra(Constantes.TVSEASONS, jsonObject.getString("tvseason"))
        if (jsonObject.has("id") && jsonObject.has("mediatype")) {
            intent.putExtra(Constantes.ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            intent.putExtra("notification", false)
            stackBuilder.addParentStack(CrewsActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callElenco(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, ElencoActivity::class.java)
        if (jsonObject.has("nome")) intent.putExtra(NOME, jsonObject.getString("nome"))
        if (jsonObject.has("mediatype")) intent.putExtra(Constantes.MEDIATYPE, jsonObject.getString("mediatype"))
        if (jsonObject.has("tvseason")) intent.putExtra(Constantes.TVSEASONS, jsonObject.getString("tvseason"))
        if (jsonObject.has("id") && jsonObject.has("mediatype")) {
            intent.putExtra(Constantes.ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            intent.putExtra("notification", false)
            stackBuilder.addParentStack(PersonActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callTrailer(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, TrailerActivity::class.java)
        if (jsonObject.has("sinopse")) intent.putExtra(Constantes.SINOPSE, jsonObject.getString("sinopse"))
        if (jsonObject.has("youtube_key")) {
            intent.putExtra(Constantes.YOU_TUBE_KEY, jsonObject.getString("youtube_key"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(TrailerActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callGenericList(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, ListaGenericaActivity::class.java)
        // Log.d("ListaGenericaActivity", "ListaGenericaActivity");
        if (jsonObject.has(NOME)) intent.putExtra(Constantes.LISTA_GENERICA, jsonObject.getString(NOME))
        if (jsonObject.has(ID)) {
            intent.putExtra(ID, jsonObject.getString(ID))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(ListaGenericaActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callMoviesList(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, MoviesActivity::class.java)
        // if (object.has("aba")) {    só funciona para NO CINEMA - ARRUMAR
        intent.putExtra(Constantes.ABA, jsonObject.getInt(ID))
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)
        stackBuilder.startActivities()
        // }
    }

    private fun callTvShow(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, TvShowActivity::class.java)
        if (jsonObject.has("color")) intent.putExtra(COLOR_TOP, jsonObject.getInt("color"))
        if (jsonObject.has(NOME)) intent.putExtra(Constantes.NOME_TVSHOW, jsonObject.getString(NOME))
        if (jsonObject.has(ID)) {
            intent.putExtra(TVSHOW_ID, jsonObject.getInt(ID))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(TvShowActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }

    private fun callMovieDetails(context: Context, jsonObject: JSONObject) {
        val intent = Intent(context, MovieDetailsActivity::class.java)
        if (jsonObject.has(COLOR_TOP)) intent.putExtra(COLOR_TOP, jsonObject.getInt("color"))
        if (jsonObject.has("id")) {
            intent.putExtra(Constantes.FILME_ID, jsonObject.getInt("id"))
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(MovieDetailsActivity::class.java)
            stackBuilder.addNextIntent(intent)
            stackBuilder.startActivities()
        }
    }
}