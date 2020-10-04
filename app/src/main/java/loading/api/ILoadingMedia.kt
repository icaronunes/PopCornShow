package loading.api

import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel.*
import domain.Imdb
import domain.ListaSeries
import domain.Movie
import domain.PersonPopular
import domain.TvSeasons
import domain.Videos
import domain.colecao.Colecao
import domain.movie.ListaFilmes
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow

interface ILoadingMedia {

    fun getDataMovie(liveData: MutableLiveData<BaseRequest<Movie>>, idMovie: Int)
    fun getMovieList(liveData: MutableLiveData<BaseRequest<ListaFilmes>>, id: String, page:Int)
    fun getMovieListByType(liveData: MutableLiveData<BaseRequest<ListaFilmes>>, type: String, page:Int)
    fun getTvListByType(liveData: MutableLiveData<BaseRequest<ListaSeries>>, type: String, page:Int)
	fun imdbDate(_imdb: MutableLiveData<BaseRequest<Imdb>>, id: String)
	fun putRated(id: Int, rated: Float, type: String)
	fun getDataTvshow(liveData: MutableLiveData<BaseRequest<Tvshow>>, idMovie: Int)
	fun getTrailerFromEn(_video: MutableLiveData<BaseRequest<Videos>>, id: Int, type: String)
	fun getDateReel(_realGood: MutableLiveData<BaseRequest<ReelGoodTv>>, idReel: String)
	fun getSeason(
		_season: MutableLiveData<BaseRequest<TvSeasons>>,
		serieId: Int,
		season_id: Int
	)

	fun putTvEpRated(id: Int, seasonNumber: Int, episodeNumber: Int, rated: Float)
	fun getMoviePopular(_movie: MutableLiveData<BaseRequest<ListaFilmes>>)
	fun getUpComing(_movie: MutableLiveData<BaseRequest<ListaFilmes>>)
	fun getTvPopular(_tvshow: MutableLiveData<BaseRequest<ListaSeries>>)
	fun fetchCollection(id: Int, _collection: MutableLiveData<BaseRequest<Colecao>>)
	fun personPopula(pager: Int, _collection: MutableLiveData<BaseRequest<PersonPopular>>)
}
