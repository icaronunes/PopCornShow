package listafilmes.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import domain.movie.ListaFilmes
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import utils.Api

class ListByTypeViewModel(app: Application, val api: Api) : BaseViewModel(app) {
	private val loadingMedia: ILoadingMedia = LoadingMedia(api)

	private val _movies: MutableLiveData<BaseRequest<ListaFilmes>> = MutableLiveData()
	val movies: LiveData<BaseRequest<ListaFilmes>> = _movies

	fun fetchListMovies(type: String, page: Int) {
		setLoading(true)
		val response = loadingMedia.getMovieListByType(type = type, page = page, liveData = _movies)
	}

	fun setLoading(loading: Boolean) {
		_movies.value = Loading(loading)
	}

}
