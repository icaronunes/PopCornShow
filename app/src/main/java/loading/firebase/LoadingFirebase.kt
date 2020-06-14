package loading.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class LoadingFirebase(type: String) : BaseFireBase(type), ILoadingFireBase {

    companion object {
        const val TV = "tv"
        const val MOVIE = "movie"
    }

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
        myFallow.addValueEventListener(valueEventFallow)
    }

    override fun isFallow(hasFallow: MutableLiveData<Boolean>, idTvshow: Int) {
        myFallow.child("$idTvshow").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
                hasFallow.value = false
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hasFallow.value = dataSnapshot.exists()
            }
        })
    }

    override fun setFallow(fallow: MutableLiveData<DataSnapshot>, add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, id: Int) {
        if (fallow.value?.child("$id")?.exists() == true) {
            remove(myFallow)
        } else {
            add(myFallow)
        }
    }

    override fun setEpWatched(fallow: MutableLiveData<DataSnapshot>, add: (DatabaseReference) -> Unit, remove: (DatabaseReference) -> Unit, id: Int) {
        if (fallow.value?.child("$id")?.exists() == true) {

        }
    }

    override fun fillSeason(_fallow: MutableLiveData<DataSnapshot>, season: HashMap<String, Any>) {
        myFallow.updateChildren(season).addOnCompleteListener {
            it
        }.addOnFailureListener {
            it
        }
    }

    override fun wathEp(childUpdates: HashMap<String, Any>) {
        myFallow.updateChildren(childUpdates)
    }

    override fun destroy() {
        myWatch.removeEventListener(valueEventWatch)
        myRated.removeEventListener(valueEventRated)
        myFavorite.removeEventListener(valueEventFavorite)
        if (type == TV) myFallow.removeEventListener(valueEventFallow)
    }

    override fun setEventListenerWatch(live: MutableLiveData<DataSnapshot>) {
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