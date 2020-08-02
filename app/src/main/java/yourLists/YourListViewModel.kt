package yourLists

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import com.google.firebase.database.DataSnapshot
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.TypeDataRef
import loading.firebase.TypeMediaFireBase

class YourListViewModel(override val app: Application) :
	BaseViewModel(app = app) {

	private val loadingFirebaseMovie: ILoadingFireBase = LoadingFirebase(TypeMediaFireBase.MOVIE)
	private val loadingFirebaseTv: ILoadingFireBase = LoadingFirebase(TypeMediaFireBase.TVSHOW)

	private val _movie: MutableLiveData<DataSnapshot> = MutableLiveData()
	var movie: LiveData<DataSnapshot> = _movie
	private val _tv: MutableLiveData<DataSnapshot> = MutableLiveData()
	var tv: LiveData<DataSnapshot> = _tv

	fun fetchDate(type: String) {
		when (type) {
			TypeDataRef.FAVORITY.type() -> {
				loadingFirebaseMovie.setEventListenerFavorit(_movie)
				loadingFirebaseTv.setEventListenerFavorit(_tv)
			}
			TypeDataRef.RATED.type() -> {
				loadingFirebaseMovie.setEventListenerRated(_movie)
				loadingFirebaseTv.setEventListenerRated(_tv)
			}
			TypeDataRef.WATCH.type() -> {
				loadingFirebaseMovie.setEventListenerWatch(_movie)
				loadingFirebaseTv.setEventListenerWatch(_tv)
			}
		}
	}

	fun removeMedia(id: Int, type: String, typeDataRef: String) {
		when (typeDataRef) {
			TypeDataRef.FAVORITY.type() -> {
				getTypeFirebase(type)?.changeFavority({
					it.child("$id").setValue(null)
				}, {
					it.child("$id").setValue(null)
				}, idMedia = id, _favorit = getType(type))
			}

			TypeDataRef.RATED.type() -> {
				getTypeFirebase(type)?.changeRated {
					it.child("$id").setValue(null)
				}
			}

			TypeDataRef.WATCH.type() -> {
				getTypeFirebase(type)?.changeWatch(add = {
					it
				}, remove = {
					it.child("$id").setValue(null)
				}, idMedia = id,
					_watch = getType(type)
				)
			}
		}
	}

	private fun getType(type: String) = when (type) {
		TypeMediaFireBase.MOVIE.type() -> _movie.value
		TypeMediaFireBase.TVSHOW.type() -> _tv.value
		else -> null
	}

	private fun getTypeFirebase(type: String) = when (type) {
		TypeMediaFireBase.MOVIE.type() -> loadingFirebaseMovie
		TypeMediaFireBase.TVSHOW.type() -> loadingFirebaseTv
		else -> null
	}
}
