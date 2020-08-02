package main

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import applicaton.BaseViewModel
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import loading.api.LoadingMedia
import utils.Api
import kotlin.coroutines.CoroutineContext

class MainViewModel(override val app: Application, val api: Api) : BaseViewModel(app),
	MainBusinessListener {

	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
			Handler(Looper.getMainLooper()).post {
				ops()
				_topo.postValue(BaseRequest.Failure(Exception(throwable)))
			}
		}

	private val business: MainBusiness = MainBusiness(app, api, this, this)
	private val loadingMedia: LoadingMedia = LoadingMedia(api)

	private val _topo = MutableLiveData<BaseRequest<Pair<ListaFilmes, ListaSeries>>>()
	val topo: LiveData<BaseRequest<Pair<ListaFilmes, ListaSeries>>> = _topo

	private val _nowPlayingMovie = MutableLiveData<BaseRequest<ListaFilmes>>()
	val nowPlayingMovie: LiveData<BaseRequest<ListaFilmes>> = _nowPlayingMovie
	private val _upComingMovie = MutableLiveData<BaseRequest<ListaFilmes>>()
	val upComingMovie: LiveData<BaseRequest<ListaFilmes>> = _upComingMovie
	private val _popularMovie = MutableLiveData<BaseRequest<ListaFilmes>>()
	val popularMovie: LiveData<BaseRequest<ListaFilmes>> = _popularMovie

	private val _airingTodayTv = MutableLiveData<BaseRequest<ListaSeries>>()
	val airingTodayTv: LiveData<BaseRequest<ListaSeries>> = _airingTodayTv

	private val _popularTv = MutableLiveData<BaseRequest<ListaSeries>>()
	val popularTv: LiveData<BaseRequest<ListaSeries>> = _popularTv

	private val _new = MutableLiveData<Boolean>()
	val new: LiveData<Boolean> = _new
	private val _animed = MutableLiveData<Boolean>()
	val animed: LiveData<Boolean> = _animed


	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun fetchTopo() = business.setTopList()

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun news() = business.setNews()

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun fetchPopularTv() = loadingMedia.getTvPopular(_popularTv)

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun fetchUpComingMovie() = loadingMedia.getUpComing(_upComingMovie)

	override fun getOps() = ops()

	override fun animation(visible: Boolean) {
		_animed.value = visible
	}

	override fun setNovidade(isNews: Boolean) {
		_new.value = isNews
	}

	override fun setTopList(movies: ListaFilmes, tvShows: ListaSeries) {
		_airingTodayTv.value = BaseRequest.Success(tvShows)
		_nowPlayingMovie.value = BaseRequest.Success(movies)
		_topo.value = BaseRequest.Success(Pair(movies, tvShows))
	}

	fun fetchAll() {
		fetchTopo()
		fetchPopularTv()
		fetchUpComingMovie()
	}
}
