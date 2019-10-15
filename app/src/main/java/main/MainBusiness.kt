package main

import android.app.Application
import android.preference.PreferenceManager
import br.com.icaro.filme.BuildConfig
import domain.Api
import java.net.ConnectException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainBusiness(val app: Application, private val mainViewModel: MainViewModel, private val listener: MainBusinessListener) {

    fun setTopLista() {
        GlobalScope.launch(mainViewModel.coroutineContext) {
            try {
                val movies = async(Dispatchers.IO) { Api(context = app).getNowPlayingMovies() }
                val tvshow = async(Dispatchers.IO) { Api(context = app).getAiringToday() }
                if (movies.isActive && tvshow.isActive)
                    listener.setTopLista(movies.await(), tvshow.await())
            } catch (ex: Exception) {
                listener.getOps()
            } catch (ex: ConnectException) {
                listener.getOps()
            }
        }
    }

    fun setNovidade() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(app)
        if (sharedPref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)) {
            listener.setNovidade(MainViewModel.MainModel.News)
        }
    }
}
