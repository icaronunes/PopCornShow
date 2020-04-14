package filme

import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.BaseRequest
import domain.Imdb
import domain.Movie
import domain.MovieDb
import domain.Videos

interface ILoadingMedia {

    fun getDataMovie(_movie: MutableLiveData<BaseRequest<Movie>>, idMovie: Int)
    fun getTrailerFromEn(_video: MutableLiveData<BaseRequest<Videos>>, idMovie: Int)
    fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String)
    fun putRated(movieDate: MovieDb)
}
