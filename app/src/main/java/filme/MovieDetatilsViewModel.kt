package filme

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.Loading
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import domain.Imdb
import domain.Movie
import domain.MovieDb
import domain.Videos
import filme.loading.LoadingFirebase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import utils.Api
import kotlin.coroutines.CoroutineContext

class MovieDetatilsViewModel(app: Application, val activity: Activity, val api: Api) : BaseViewModel(app) {

    private val coroutineContextGuestStarsItem: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, _ ->
            Handler(Looper.getMainLooper()).post {
                ops()
                Crashlytics.log("Error Rated Movie")
            }
        }

    private val loadingFirebase = LoadingFirebase("movie")
    private val loadingMedia = LoadingMedia(api)

    val auth: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _watch: MutableLiveData<DataSnapshot> = MutableLiveData()
    val watch: LiveData<DataSnapshot> = _watch
    private val _favorit: MutableLiveData<DataSnapshot> = MutableLiveData()
    val favorit: LiveData<DataSnapshot> = _favorit
    private val _rated: MutableLiveData<DataSnapshot> = MutableLiveData()
    val rated: LiveData<DataSnapshot> = _rated
    private val _movie: MutableLiveData<BaseRequest<Movie>> = MutableLiveData()
    val movie: LiveData<BaseRequest<Movie>> = _movie
    private val _videos: MutableLiveData<BaseRequest<Videos>> = MutableLiveData()
    val videos: LiveData<BaseRequest<Videos>> = _videos
    private val _imdb: MutableLiveData<BaseRequest<Imdb>> = MutableLiveData()
    val imdb: LiveData<BaseRequest<Imdb>> = _imdb

    fun destroy() {
        loadingFirebase.destroy()
    }

    init {
        isAuth()
        setEventListenerFavorit()
        setEventListenerRated()
        setEventListenerWatch()
    }

    private fun isAuth() = loadingFirebase.isAuth(auth)
    private fun setEventListenerWatch() = loadingFirebase.setEventListenerWatch(_watch)
    private fun setEventListenerFavorit() = loadingFirebase.setEventListenerFavorit(_favorit)
    private fun setEventListenerRated() = loadingFirebase.setEventListenerRated(_rated)

    fun chanceWatch(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int) {
        loadingFirebase.changeWatch(add, remove, idMedia, _watch.value)
    }

    fun executeFavority(remove: (DatabaseReference) -> Unit, add: (DatabaseReference) -> Unit, idMovie: Int) {
        loadingFirebase.changeFavority(remove, add, idMovie, _favorit.value)
    }

    fun changeRated(change: (DatabaseReference) -> Unit, idMovie: Int) = loadingFirebase.changeRated(change, idMovie)
    fun setRatedOnTheMovieDB(movieDate: MovieDb) = loadingMedia.putRated(movieDate)
    fun getDataMovie(idMovie: Int) = loadingMedia.getDataMovie(_movie, idMovie)
    fun getTrailerEn(idMovie: Int) = loadingMedia.getTrailerFromEn(_videos, idMovie)
    fun getImdb(id: String) = loadingMedia.imdbDate(_imdb, id)

    fun setLoading(boolean: Boolean) { _movie.value = Loading(boolean) }
}