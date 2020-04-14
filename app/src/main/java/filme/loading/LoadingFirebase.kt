package filme.loading

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class LoadingFirebase(type: String) : BaseFireBase(type) {

    private lateinit var valueEventWatch: ValueEventListener
    private lateinit var valueEventRated: ValueEventListener
    private lateinit var valueEventFavorite: ValueEventListener

    fun isAuth(live: MutableLiveData<Boolean>) {
        live.value = isAuth()
    }

    fun destroy() {
        myWatch.removeEventListener(valueEventWatch)
        myRated.removeEventListener(valueEventRated)
        myFavorite.removeEventListener(valueEventFavorite)
    }

    fun setEventListenerWatch(live: MutableLiveData<DataSnapshot>) {
        valueEventWatch = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myWatch.addValueEventListener(valueEventWatch)
    }

    fun setEventListenerFavorit(live: MutableLiveData<DataSnapshot>) {
        valueEventFavorite = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myFavorite.addValueEventListener(valueEventFavorite)
    }

    fun setEventListenerRated(live: MutableLiveData<DataSnapshot>) {
        valueEventRated = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                live.value = dataSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRated.addValueEventListener(valueEventRated)
    }

    fun changeWatch(add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, idMedia: Int, _watch: DataSnapshot?) {
        if (_watch?.child("$idMedia")?.exists() == true) {
            remove(myWatch)
        } else {
            add(myWatch)
        }
    }

    fun changeFavority(add: (DatabaseReference) -> Unit,
        remove: (DatabaseReference) -> Unit,
        idMedia: Int,
        _favorit: DataSnapshot?) {
        if (_favorit?.child("$idMedia")?.exists() == true) {
            add(myFavorite)
        } else {
            remove(myFavorite)
        }
    }

    fun changeRated(add: (DatabaseReference) -> Unit, idMovie: Int) {
         add(myRated)
    }
}