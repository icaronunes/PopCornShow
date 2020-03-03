package main

import android.app.Application
import android.preference.PreferenceManager
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.BuildConfig
import utils.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainBusiness(val app: Application, private val mainViewModel: BaseViewModel, private val listener: MainBusinessListener) {

    fun setTopList() {
        GlobalScope.launch(mainViewModel.coroutineContext) {
            val movies = async(Dispatchers.IO) { Api(context = app).getNowPlayingMovies() as Success<ListaFilmes> }
            val tvshow = async(Dispatchers.IO) { Api(context = app).getAiringToday() as Success<ListaSeries> }
            listener.setTopList(movies.await().result, tvshow.await().result)
        }
    }

    fun setNews() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(app)
        if (sharedPref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)) {
            listener.setNovidade(MainViewModel.MainModel.News)
        }
    }
}
