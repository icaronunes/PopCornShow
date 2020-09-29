package loading.api

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.*
import applicaton.BaseViewModel.BaseRequest.*
import domain.Imdb
import domain.ListaSeries
import domain.Movie
import domain.TvSeasons
import domain.Videos
import domain.colecao.Colecao
import domain.movie.ListaFilmes
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Api

class LoadingMedia(val api: Api) : ILoadingMedia { // TODO injetar na viewmodels
	// Remover esses PostValue ... pode causar leak
	override fun getDataMovie(liveData: MutableLiveData<BaseRequest<Movie>>, idMovie: Int) {
		GlobalScope.launch {
			liveData.postValue(withContext(Dispatchers.Default) { api.getMovie(idMovie) })
		}
	}

	override fun getMovieList(
		liveData: MutableLiveData<BaseRequest<ListaFilmes>>,
		id: Int,
		page: Int,
	) {
		GlobalScope.launch {
			liveData.postValue(withContext(Dispatchers.Default) { api.getListMovie(id, page) })
		}
	}
	override fun getMovieListByType(
		liveData: MutableLiveData<BaseRequest<ListaFilmes>>,
		type: String,
		page: Int,
	) {
		GlobalScope.launch {
			liveData.postValue(withContext(Dispatchers.Default) { api.getListMovieByType(type, page) })
		}
	}

	override fun getDataTvshow(liveData: MutableLiveData<BaseRequest<Tvshow>>, idMovie: Int) {
		liveData.postValue(Loading(true))
		GlobalScope.launch {
			liveData.postValue(withContext(Dispatchers.Default) { api.getTvshow(idMovie) })
		}
	}

	override fun getTrailerFromEn(
		_video: MutableLiveData<BaseRequest<Videos>>,
		id: Int,
		type: String,
	) {
		GlobalScope.launch {
			_video.postValue(withContext(Dispatchers.Default) {
				api.getTrailersFromEn(id, type)
			})
		}
	}

	override fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String) {
		GlobalScope.launch(handle(_imdb)) {
			_imdb.postValue(withContext(Dispatchers.Default) {
				api.getResquestImdb(id)
			})
		}
	}

	override fun putRated(id: Int, rated: Float, type: String) {
		GlobalScope.launch {
			val guest = withContext(Dispatchers.Default) { api.userGuest() }
			if (guest is Success) api.ratedMediaGuest(id, rated, guest.result, type)
		}
	}

	override fun getDateReel(_realGood: MutableLiveData<BaseRequest<ReelGoodTv>>, idReel: String) {
		GlobalScope.launch(Dispatchers.Default + SupervisorJob() + CoroutineExceptionHandler { _, erro ->
			Handler(Looper.getMainLooper()).post {
				_realGood.postValue(BaseRequest.Failure(java.lang.Exception(erro.cause)))
			}
		}) {
			val respose = api.getAvaliableShow(idReel)
			_realGood.postValue(Success(respose))
		}
	}

	override fun getSeason(
		_season: MutableLiveData<BaseRequest<TvSeasons>>,
		serieId: Int,
		season_id: Int,
	) {
		GlobalScope.launch(handle(_season)) {
			val response = api.getTvSeasons(serieId, season_id)
			_season.postValue(Success(response))
		}
	}

	override fun putTvEpRated(id: Int, seasonNumber: Int, episodeNumber: Int, rated: Float) {
		GlobalScope.launch {
			val guest = withContext(Dispatchers.Default) { api.userGuest() }
			if (guest is Success) api.ratedTvEpsodeeGuest(
				id,
				seasonNumber,
				episodeNumber,
				rated,
				guest.result.guestSessionId
			)
		}
	}

	override fun getUpComing(_movie: MutableLiveData<BaseRequest<ListaFilmes>>) {
		GlobalScope.launch(handle(_movie)) {
			val upComing = api.getUpcoming()
			_movie.postValue(Success(upComing))
		}
	}

	override fun getMoviePopular(_movie: MutableLiveData<BaseRequest<ListaFilmes>>) {
		GlobalScope.launch(handle(_movie)) {
			val popular = api.getMoviePopular()
			_movie.postValue(Success(popular))
		}
	}

	override fun getTvPopular(_tvshow: MutableLiveData<BaseRequest<ListaSeries>>) {
		GlobalScope.launch(handle(_tvshow)) {
			val popular = api.getPopularTv()
			_tvshow.postValue(Success(popular))
		}
	}

	override fun fetchCollection(id: Int, _collection: MutableLiveData<BaseRequest<Colecao>>) {
		GlobalScope.launch(handle(_collection)) {
			val colection = api.getCollection(id)
			_collection.postValue((Success(colection)))
		}
	}

	private fun <T> handle(_live: MutableLiveData<BaseRequest<T>> = MutableLiveData()) =
		Dispatchers.Default + SupervisorJob() + CoroutineExceptionHandler { _, erro ->
			Handler(Looper.getMainLooper()).post {
				_live.postValue(BaseRequest.Failure(java.lang.Exception(erro.cause)))
			}
		}
}
