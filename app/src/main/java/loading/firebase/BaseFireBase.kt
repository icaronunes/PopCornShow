package loading.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class BaseFireBase(val type: String) {

    companion object {
        const val WATCH = "watch"
        const val FAVORITY = "favorites"
        const val RATED = "rated"
        const val USERS = "users"
        const val FALLOW = "seguindo"
    }

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    lateinit var myWatch: DatabaseReference
    lateinit var myFavorite: DatabaseReference
    lateinit var myRated: DatabaseReference
    lateinit var myFallow: DatabaseReference

    init {
        iniFirebase()
    }

    private fun iniFirebase() {

            myWatch = database.getReference(USERS).child(firebaseAuth.currentUser
                ?.uid ?: "").child(WATCH)
                .child(type)

            myFavorite = database.getReference(USERS).child(firebaseAuth.currentUser
                ?.uid ?: "").child(FAVORITY)
                .child(type)

            myRated = database.getReference(USERS).child(firebaseAuth.currentUser
                ?.uid ?: "").child(RATED)
                .child(type)

             myFallow = database.getReference(USERS).child(firebaseAuth.currentUser
                 ?.uid ?: "").child(FALLOW)
    }

    fun isAuth() = firebaseAuth.currentUser != null
    fun getDatabase() = database
}
