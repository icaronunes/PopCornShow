package loading.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import domain.tvshow.Tvshow

class LoadingFirebase(type: String) : BaseFireBase(type), ILoadingFireBase {

    private lateinit var valueEventWatch: ValueEventListener
    private lateinit var valueEventRated: ValueEventListener
    private lateinit var valueEventFavorite: ValueEventListener
    private lateinit var valueEventFallow: ValueEventListener

   override fun isAuth(live: MutableLiveData<Boolean>) {
        live.value = isAuth()
    }

    override fun isWatching(live: MutableLiveData<DataSnapshot>, idTvshow: Int) {
        valueEventFallow = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        myFallow.addListenerForSingleValueEvent(valueEventFallow)
    }

    override fun destroy() {
        myWatch.removeEventListener(valueEventWatch)
        myRated.removeEventListener(valueEventRated)
        myFavorite.removeEventListener(valueEventFavorite)
        myFallow.removeEventListener(valueEventFallow)
    }

    override  fun setEventListenerWatch(live: MutableLiveData<DataSnapshot>) {
        valueEventWatch = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myWatch.addValueEventListener(valueEventWatch)
    }

    override fun setEventListenerFavorit(live: MutableLiveData<DataSnapshot>) {
        valueEventFavorite = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myFavorite.addValueEventListener(valueEventFavorite)
    }

    override fun setEventListenerRated(live: MutableLiveData<DataSnapshot>) {
        valueEventRated = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRated.addValueEventListener(valueEventRated)
    }

    override fun changeWatch(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int, _watch: DataSnapshot?) {
        if (_watch?.child("$idMedia")?.exists() == true) {
            remove(myWatch)
        } else {
            add(myWatch)
        }
    }

    override fun changeFavority(add: (DatabaseReference) -> Unit,
        remove: (DatabaseReference) -> Unit,
        idMedia: Int,
        _favorit: DataSnapshot?) {
        if (_favorit?.child("$idMedia")?.exists() == true) {
            add(myFavorite)
        } else {
            remove(myFavorite)
        }
    }

    override fun changeRated(add: (DatabaseReference) -> Unit, idMovie: Int) {
         add(myRated)
    }
}