package filme

import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.BaseRequest
import applicaton.BaseViewModel.BaseRequest.Success
import domain.Imdb
import domain.Movie
import domain.MovieDb
import domain.Videos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Api

class LoadingMedia(val api: Api): ILoadingMedia {

    override fun getDataMovie(_movie: MutableLiveData<BaseRequest<Movie>>, idMovie: Int) {
        GlobalScope.launch {
                _movie.postValue(withContext(Dispatchers.Default) { api.getMovie(idMovie) })
        }
    }

    override fun getTrailerFromEn(_video: MutableLiveData<BaseRequest<Videos>>, idMovie: Int) {
        GlobalScope.launch {
            _video.postValue(withContext(Dispatchers.Default) {
                api.getTrailersFromEn(idMovie)
            })
        }
    }

    override fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String) {
        GlobalScope.launch {
            _imdb.postValue(withContext(Dispatchers.Default) {
                    api.getImdb(id)
            })
        }
    }

    override fun putRated(movieDate: MovieDb) {
        GlobalScope.launch() {
            val guest = withContext(Dispatchers.Default) { api.userGuest() }
            if (guest is Success) api.ratedMovieGuest(movieDate, guest.result)

        }
    }
}
