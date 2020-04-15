package tvshow.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import domain.Movie
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import utils.Api

class TvShowViewModel(override val app: Application, activity: Activity, api: Api): BaseViewModel(app) {

    val loadingFirebase: ILoadingFireBase = LoadingFirebase("tvshow")

    val auth: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _watch: MutableLiveData<DataSnapshot> = MutableLiveData()
    val watch: LiveData<DataSnapshot> = _watch
    private val _favorit: MutableLiveData<DataSnapshot> = MutableLiveData()
    val favorit: LiveData<DataSnapshot> = _favorit
    private val _rated: MutableLiveData<DataSnapshot> = MutableLiveData()
    val rated: LiveData<DataSnapshot> = _rated
    private val _movie: MutableLiveData<BaseRequest<Movie>> = MutableLiveData()
    val movie: LiveData<BaseRequest<Movie>> = _movie

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
}
