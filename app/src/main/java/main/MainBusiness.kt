package main

import android.app.Application
import android.content.Context
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.BuildConfig
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import utils.Api

class MainBusiness(
	val app: Application,
	val api: Api,
	private val mainViewModel: BaseViewModel,
	private val listener: MainBusinessListener
) {
	fun setTopList() {
		GlobalScope.launch(mainViewModel.coroutineContext) {
			val movies = async(Dispatchers.IO) { api.getNowPlayingMovies() as Success<ListaFilmes> }
			val tvshow = async(Dispatchers.IO) { api.getAiringToday() as Success<ListaSeries> }
			listener.animation(false)
			listener.setTopList(movies.await().result, tvshow.await().result)
		}
	}

	fun setNews(activity: MainActivity) {
		val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
		listener.setNovidade(sharedPref.getBoolean((BuildConfig.VERSION_CODE).toString(), true))
	}
}
