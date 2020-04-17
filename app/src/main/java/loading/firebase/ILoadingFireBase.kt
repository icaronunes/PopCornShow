package loading.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

interface ILoadingFireBase {

    fun setEventListenerRated(live: MutableLiveData<DataSnapshot>)
    fun changeWatch(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int, _watch: DataSnapshot?)
    fun changeFavority(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int, _favorit: DataSnapshot?)
    fun changeRated(add: (DatabaseReference) -> Unit, idMovie: Int)
    fun isAuth(live: MutableLiveData<Boolean>)
    fun destroy()
    fun setEventListenerWatch(live: MutableLiveData<DataSnapshot>)
    fun setEventListenerFavorit(live: MutableLiveData<DataSnapshot>)
    fun isWatching(live: MutableLiveData<DataSnapshot>, idTvshow: Int)
}
