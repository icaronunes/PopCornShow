package listafilmes.viewmodel

import ILoadingAd
import LoadingAd
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ListaSeries
import domain.movie.ListaFilmes
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import utils.Api

class ListByTypeViewModel(app: Application, val api: Api) : BaseViewModel(app) {
	private val loadingMedia: ILoadingMedia = LoadingMedia(api)
	private val loadingAds: ILoadingAd = LoadingAd(app.baseContext)

	private val _movies: MutableLiveData<BaseRequest<ListaFilmes>> = MutableLiveData()
	val movies: LiveData<BaseRequest<ListaFilmes>> = _movies

	private val _tvshows: MutableLiveData<BaseRequest<ListaSeries>> = MutableLiveData()
	val tvshows: LiveData<BaseRequest<ListaSeries>> = _tvshows
	val ads: MutableLiveData<MutableList<UnifiedNativeAd>> = MutableLiveData(mutableListOf())

	private val _moviesList: MutableLiveData<BaseRequest<ListaFilmes>> = MutableLiveData()
	val moviesList: LiveData<BaseRequest<ListaFilmes>> = _moviesList

	fun fetchListMovies(type: String, page: Int) {
		setLoadingMovie(true)
		loadingMedia.getMovieListByType(type = type, page = page, liveData = _movies)
	}

	fun fetchListTvshow(type: String, page: Int) {
		setLoadingTv(true)
		loadingMedia.getTvListByType(type = type, page = page, liveData = _tvshows)
	}

	fun fetchListById(id: Int, page: Int) {
		setLoadingMovieId(true)
		loadingMedia.getMovieList(_moviesList, id, page )
	}

	fun setLoadingMovieId(loading: Boolean) {
		_moviesList.value = Loading(loading)
	}

	fun setLoadingMovie(loading: Boolean) {
		_movies.value = Loading(loading)
	}

	fun setLoadingTv(loading: Boolean) {
		_tvshows.value = Loading(loading)
	}

	fun fillAdNative() {
		loadingAds.fillAdNative(ads)
	}
}
