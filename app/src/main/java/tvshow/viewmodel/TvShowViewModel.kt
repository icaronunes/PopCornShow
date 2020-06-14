package tvshow.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.Loading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import domain.Imdb
import domain.TvshowDB
import domain.Videos
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.LoadingFirebase.Companion.TV
import utils.Api

class TvShowViewModel(override val app: Application, activity: Activity, val api: Api) : BaseViewModel(app) {

    private val loadingFirebase: ILoadingFireBase = LoadingFirebase(TV)
    private val loadingMedia: ILoadingMedia = LoadingMedia(api)

    val auth: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFallow: MutableLiveData<Boolean> = MutableLiveData(false)
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
    val tvshow: LiveData<BaseRequest<Tvshow>> = _tvshow
    private val _real: MutableLiveData<BaseRequest<ReelGoodTv>> = MutableLiveData()
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

    fun setRatedOnTheMovieDB(tvshowDB: TvshowDB) {
        loadingMedia.putRated(tvshowDB.id, tvshowDB.nota, "tv")
    }

    fun getRealGoodData(idReel: String) {
        loadingMedia.getDateReel(_realGood = _real, idReel = idReel)
    }

    fun fallow(idTvshow: Int) = loadingFirebase.isWatching(_fallow, idTvshow = idTvshow)
    fun hasfallow(idTvshow: Int) = loadingFirebase.isFallow(isFallow, idTvshow = idTvshow)

    fun getImdb(id: String) = loadingMedia.imdbDate(_imdb, id)
    fun getTrailerEn(idMovie: Int) = loadingMedia.getTrailerFromEn(_videos, idMovie, "tv")
    fun getTvshow(idTvshow: Int) = loadingMedia.getDataTvshow(_tvshow, idMovie = idTvshow)
    fun loading(loading: Boolean) {
        _tvshow.value = Loading(true) //Todo Ta certo?
    }

    fun fillSeason(childUpdates: HashMap<String, Any>) {
        loadingFirebase.fillSeason(_fallow, childUpdates)
    }
}