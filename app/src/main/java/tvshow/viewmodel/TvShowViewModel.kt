package tvshow.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.Loading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import domain.Imdb
import domain.TvSeasons
import domain.TvshowDB
import domain.UserEp
import domain.Videos
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.TypeDataRef
import loading.firebase.TypeMediaFireBase
import utils.Api

class TvShowViewModel(override val app: Application, val api: Api) : BaseViewModel(app) {
    private val TV = "tv"
    private val loadingFirebase: ILoadingFireBase = LoadingFirebase(TypeMediaFireBase.TVSHOW)
    private val loadingMedia: ILoadingMedia = LoadingMedia(api)

    val auth: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFallow: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingView: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _watch: MutableLiveData<DataSnapshot> = MutableLiveData()
    val watch: LiveData<DataSnapshot> = _watch
    private val _favorit: MutableLiveData<DataSnapshot> = MutableLiveData()
    val favorit: LiveData<DataSnapshot> = _favorit
    private val _rated: MutableLiveData<DataSnapshot> = MutableLiveData()
    val rated: LiveData<DataSnapshot> = _rated
    private val _fallow: MutableLiveData<DataSnapshot> = MutableLiveData()
    val fallow: LiveData<DataSnapshot> = _fallow
    private val _imdb: MutableLiveData<BaseRequest<Imdb>> = MutableLiveData()
    val imdb: LiveData<BaseRequest<Imdb>> = _imdb
    private val _videos: MutableLiveData<BaseRequest<Videos>> = MutableLiveData()
    val videos: LiveData<BaseRequest<Videos>> = _videos
    private val _tvshow: MutableLiveData<BaseRequest<Tvshow>> = MutableLiveData()
    val tvShow: LiveData<BaseRequest<Tvshow>> = _tvshow
    private val _tvSeason: MutableLiveData<BaseRequest<TvSeasons>> = MutableLiveData()
    val tvSeason: LiveData<BaseRequest<TvSeasons>> = _tvSeason
    private val _real: MutableLiveData<BaseRequest<ReelGoodTv>> = MutableLiveData()
    private val _seasons: MutableLiveData<DataSnapshot> = MutableLiveData()
    val seasons: LiveData<DataSnapshot> = _seasons
    val real: LiveData<BaseRequest<ReelGoodTv>> = _real

    init {
        isAuth()
        setEventListenerWatch()
        setEventListenerFavorit()
        setEventListenerRated()
    }

    private fun isAuth() = loadingFirebase.isAuth(auth)
    private fun setEventListenerWatch() = loadingFirebase.setEventListenerWatch(_watch)
    private fun setEventListenerFavorit() = loadingFirebase.setEventListenerFavorit(_favorit)
    private fun setEventListenerRated() = loadingFirebase.setEventListenerRated(_rated)

    fun destroy() {
        loadingFirebase.destroy()
    }

    fun putFavority(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, id: Int) {
        loadingFirebase.changeFavority(add = add, remove = remove, idMedia = id, _favorit = _favorit.value)
    }

    fun setRated(id: Int, change: (DatabaseReference) -> Unit) {
        loadingFirebase.changeRated(change, id)
    }

    fun chanceWatch(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int) {
        loadingFirebase.changeWatch(add, remove, idMedia, _watch.value)
    }

    fun setFallow(idTvshow: Int, add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit) {
        loadingFirebase.setFallow(_fallow, add, remove, idTvshow)
    }

    fun watchEp(childUpdates: HashMap<String, Any>) {
        loadingFirebase.wathEp(childUpdates)
    }

    fun setRatedOnTheMovieDB(tvshowDB: TvshowDB) { loadingMedia.putRated(tvshowDB.id, tvshowDB.nota, TV) }
    fun setRatedTvShowOnTheMovieDB(tvshowId: Int, epRated: UserEp) {
        loadingMedia.putTvEpRated(tvshowId, epRated.seasonNumber, epRated.episodeNumber, epRated.nota)
    }
    fun getRealGoodData(idReel: String) { loadingMedia.getDateReel(_realGood = _real, idReel = idReel) }

    fun getImdb(id: String) = loadingMedia.imdbDate(_imdb, id)
    fun getTrailerEn(idMovie: Int) = loadingMedia.getTrailerFromEn(_videos, idMovie, TV)
    fun getTvshow(idTvshow: Int) = loadingMedia.getDataTvshow(_tvshow, idMovie = idTvshow)
    fun getSeason(idTvshow: Int, seasonNumber: Int) =
        loadingMedia.getSeason(_tvSeason, idTvshow, seasonNumber)

    fun loadingMedia(loading: Boolean) {
        _tvshow.value = Loading(loading)
    }

    fun loadingView(loading: Boolean) {
        loadingView.value = loading
    }

    fun fallow(idTvshow: Int) = loadingFirebase.isWatching(_fallow, idTvshow = idTvshow)
    fun hasfallow(idTvshow: Int) = loadingFirebase.isFallow(isFallow, idTvshow = idTvshow)
    fun fillSeason(childUpdates: HashMap<String, Any>) {
        loadingFirebase.fillSeason(_fallow, childUpdates)
    }

    fun getSeasonFire(idTvshow: Int, seasonNumber: Int) =
        loadingFirebase.fillSeasons(idTvshow, seasonNumber, _seasons)

    fun update(
        id: Int, childUpdates: HashMap<String, Any?>, type: TypeDataRef
    ) {
        loadingFirebase.upDateTvDetails(id, childUpdates, type)
    }
}
