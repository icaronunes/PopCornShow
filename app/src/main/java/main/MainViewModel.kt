package main

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import applicaton.BaseViewModel
import br.com.icaro.filme.BuildConfig
import domain.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException

class MainViewModel(override val app: Application) : BaseViewModel(app) {

    private val _data = MutableLiveData<MainModel>()
    val data: LiveData<MainModel>
        get() = _data

    fun getTopoLista() {
        GlobalScope.launch(coroutineContext) {
            try {
                val movies = async(Dispatchers.IO) { Api(context = app).getNowPlayingMovies() }
                val tvshow = async(Dispatchers.IO) { Api(context = app).getAiringToday() }
                if (movies.isActive && tvshow.isActive)
                    _data.value = MainModel.Data(Pair(movies.await(), tvshow.await()))
            } catch (ex: Exception) {
                ops()
            } catch (ex: ConnectException) {
                ops()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START) // Funciona não!
    fun novidade() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(app)
        if (sharedPref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)) {
            _data.value = MainModel.isNovidade
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun animation(visible: Boolean = true) {
        _data.value = MainModel.VisibleAnimed(visible)
    }

    sealed class MainModel {
        class Data(val data: Pair<ListaFilmes, ListaSeries>) : MainModel()
        class VisibleAnimed(val visible: Boolean) : MainModel()
        object isNovidade : MainModel()
    }
}