package loading.api

import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.BaseRequest
import domain.Imdb
import domain.Movie
import domain.Videos
import domain.tvshow.Tvshow

interface ILoadingMedia {

    fun getDataMovie(_movie: MutableLiveData<BaseRequest<Movie>>, idMovie: Int)
    fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String)
    fun putRated(id: Int, rated: Float, type: String)
    fun getDataTvshow(_movie: MutableLiveData<BaseRequest<Tvshow>>, idMovie: Int)
    fun getTrailerFromEn(_video: MutableLiveData<BaseRequest<Videos>>, id: Int, type: String)
}
