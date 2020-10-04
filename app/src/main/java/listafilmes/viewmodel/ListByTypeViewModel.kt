package listafilmes.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import domain.ListaSeries
import domain.movie.ListaFilmes
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import utils.Api

class ListByTypeViewModel(app: Application, val api: Api) : BaseViewModel(app) {
	private val loadingMedia: ILoadingMedia = LoadingMedia(api)

	private val _movies: MutableLiveData<BaseRequest<ListaFilmes>> = MutableLiveData()
	val movies: LiveData<BaseRequest<ListaFilmes>> = _movies
	private val _tvshows: MutableLiveData<BaseRequest<ListaSeries>> = MutableLiveData()
	val tvshows: LiveData<BaseRequest<ListaSeries>> = _tvshows

	fun fetchListMovies(type: String, page: Int) {
		setLoadingMovie(true)
		loadingMedia.getMovieListByType(type = type, page = page, liveData = _movies)
	}

	fun fetchListTvshow(type: String, page: Int) {
		setLoadingTv(true)
		loadingMedia.getTvListByType(type = type, page = page, liveData = _tvshows)
	}

	fun setLoadingMovie(loading: Boolean) {
		_movies.value = Loading(loading)
	}

	fun setLoadingTv(loading: Boolean) {
		_tvshows.value = Loading(loading)
	}

}
