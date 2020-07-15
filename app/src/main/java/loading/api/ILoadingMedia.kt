package loading.api

import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.BaseRequest
import domain.Imdb
import domain.Movie
import domain.TvSeasons
import domain.Videos
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow

interface ILoadingMedia {

    fun getDataMovie(liveData: MutableLiveData<BaseRequest<Movie>>, idMovie: Int)
    fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String)
    fun putRated(id: Int, rated: Float, type: String)
    fun getDataTvshow(liveData: MutableLiveData<BaseRequest<Tvshow>>, idMovie: Int)
    fun getTrailerFromEn(_video: MutableLiveData<BaseRequest<Videos>>, id: Int, type: String)
    fun getDateReel(_realGood: MutableLiveData<BaseRequest<ReelGoodTv>>, idReel: String)
	fun getSeason(
		_season: MutableLiveData<BaseRequest<TvSeasons>>,
		serieId: Int,
		season_id: Int)
	fun putTvEpRated(id: Int, seasonNumber: Int, episodeNumber: Int, rated: Float)
}
