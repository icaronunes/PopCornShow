package loading.api

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.BaseRequest
import applicaton.BaseViewModel.BaseRequest.Loading
import applicaton.BaseViewModel.BaseRequest.Success
import domain.Imdb
import domain.Movie
import domain.Videos
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Api

class LoadingMedia(val api: Api) : ILoadingMedia {

	override fun getDataMovie(_movie: MutableLiveData<BaseRequest<Movie>>, idMovie: Int) {
		GlobalScope.launch {
			_movie.postValue(withContext(Dispatchers.Default) { api.getMovie(idMovie) })
		}
	}

	override fun getDataTvshow(_tvshow: MutableLiveData<BaseRequest<Tvshow>>, idMovie: Int) {
		_tvshow.postValue(Loading(true))
		GlobalScope.launch {
			_tvshow.postValue(withContext(Dispatchers.Default) { api.getTvshow(idMovie) })
		}
	}

	override fun getTrailerFromEn(
		_video: MutableLiveData<BaseRequest<Videos>>,
		id: Int,
		type: String
	) {
		GlobalScope.launch {
			_video.postValue(withContext(Dispatchers.Default) {
				api.getTrailersFromEn(id, type)
			})
		}
	}

	override fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String) {
		GlobalScope.launch {
			_imdb.postValue(withContext(Dispatchers.Default) {
				api.getResquestImdb(id)
			})
		}
	}

	override fun putRated(id: Int, rated: Float, type: String) {
		GlobalScope.launch {
			val guest = withContext(Dispatchers.Default) { api.userGuest() }
			if (guest is Success) api.ratedMovieGuest(id, rated, guest.result, type)
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
}
