package filme.activity

import activity.BaseActivityAb
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.icaro.filme.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.FilmeDB
import domain.FilmeService
import domain.Movie
import filme.fragment.MovieFragment
import fragment.ImagemTopFilmeScrollFragment
import kotlinx.android.synthetic.main.activity_movie.filme_container
import kotlinx.android.synthetic.main.activity_movie.streamview_movie
import kotlinx.android.synthetic.main.activity_movie.top_img_viewpager
import kotlinx.android.synthetic.main.fab_float.fab_menu
import kotlinx.android.synthetic.main.fab_float.menu_item_favorite
import kotlinx.android.synthetic.main.fab_float.menu_item_rated
import kotlinx.android.synthetic.main.fab_float.menu_item_watchlist
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Api
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.setAnimation
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovieDetailsActivity(override var layout: Int = R.layout.activity_movie) : BaseActivityAb() {
    private lateinit var movieFragment: MovieFragment
    private var color: Int = 0
    private var idMovie: Int = 0
    private var movieDb: Movie? = null
    private var addFavorite = true
    private var addWatch = true
    private var addRated = true

    private var mAuth: FirebaseAuth? = null

    private var myWatch: DatabaseReference? = null
    private var myFavorite: DatabaseReference? = null
    private var myRated: DatabaseReference? = null

    private var valueEventWatch: ValueEventListener? = null
    private var valueEventRated: ValueEventListener? = null
    private var valueEventFavorite: ValueEventListener? = null

    private var numberRated: Float = 0.0f

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing Api.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolBar()
        setupNavDrawer()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleAndDisableTalk(" ")
        getExtras()

        top_img_viewpager.apply {
            setBackgroundColor(color)
            offscreenPageLimit = 3
        }

        iniciarFirebases()
        subscriptions = CompositeSubscription()

        if (UtilsApp.isNetWorkAvailable(this)) {
            getDados()
        } else {
            snack()
        }
    }

    private fun getDados() {

        val inscricaoMovie = Api(context = this).loadMovieComVideo(idMovie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                movieDb = it
                title = movieDb?.title
                top_img_viewpager?.adapter = ImagemTopFragment(supportFragmentManager)
                progress_horizontal?.visibility = View.GONE
                setFAB()
                setFragmentInfo()
                setStream()
            }, {
                Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
            })

        subscriptions.add(inscricaoMovie)
    }

    fun getIdStream(): String {
        return try {
            "${movieDb?.originalTitle?.getNameTypeReel()
                ?: ""}-${movieDb?.releaseDate?.substring(0, 4)}"
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    private fun setStream() {
        GlobalScope.launch(Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, erro ->
            Handler(Looper.getMainLooper()).post {
                streamview_movie.error = true
                setAnimated()
            }
        }) {
            val reelGood = async(Dispatchers.IO) {
                Api(this@MovieDetailsActivity).getAvaliableMovie(getIdStream())
            }.await()

            if (reelGood.availability.isNotEmpty()) {
                streamview_movie.titleMovie = movieDb?.originalTitle ?: ""
                streamview_movie.stream = reelGood.availability.filter {
                    it.accessType == 2
                }

                streamview_movie.bay = reelGood.availability.filter {
                    it.accessType == 3
                }

                streamview_movie.rent = reelGood.availability.filter {
                    it.accessType == 3
                }

                delay(50)
                if (streamview_movie.isListsEmpty()) streamview_movie.error = true
            } else {
                streamview_movie.error = true
            }
            setAnimated()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setAnimated() {
        // Todo colocar dentro do Stream
        val sheet = BottomSheetBehavior.from(streamview_movie)
        (sheet as? BottomSheetBehavior<View>)?.setAnimation(filme_container, streamview_movie.findViewById(R.id.title_streaming))
        streamview_movie.setClose(sheet)
    }

    private fun setEventListenerWatch() {

        valueEventWatch = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(idMovie.toString()).exists()) {
                    addWatch = true
                    menu_item_watchlist?.labelText = resources.getString(R.string.remover_watch)
                } else {
                    addWatch = false
                    menu_item_watchlist?.labelText = resources.getString(R.string.adicionar_watch)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myWatch?.addValueEventListener(valueEventWatch!!)
    }

    private fun setEventListenerRated() {
        valueEventRated = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(idMovie.toString()).exists()) {
                    addRated = true

                    if (dataSnapshot.child(idMovie.toString()).child("nota").exists()) {
                        val nota = dataSnapshot.child(idMovie.toString()).child("nota").value.toString()
                        numberRated = java.lang.Float.parseFloat(nota)
                        menu_item_rated?.labelText = resources.getString(R.string.remover_rated)
                        if (numberRated == 0.0f) {
                            menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
                        }
                    }
                } else {
                    addRated = false
                    numberRated = 0.0f
                    menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myRated?.addValueEventListener(valueEventRated!!)
    }

    private fun setEventListenerFavorite() {
        valueEventFavorite = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(idMovie.toString()).exists()) {
                    addFavorite = true
                    menu_item_favorite?.labelText = resources.getString(R.string.remover_favorite)
                } else {
                    addFavorite = false
                    menu_item_favorite?.labelText = resources.getString(R.string.adicionar_favorite)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        myFavorite?.addValueEventListener(valueEventFavorite!!)
    }

    private fun iniciarFirebases() {

        mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        if (mAuth?.currentUser != null) {

            myWatch = database.getReference("users").child(mAuth?.currentUser
                ?.uid!!).child("watch")
                .child("movie")

            myFavorite = database.getReference("users").child(mAuth?.currentUser
                ?.uid!!).child("favorites")
                .child("movie")

            myRated = database.getReference("users").child(mAuth?.currentUser
                ?.uid!!).child("rated")
                .child("movie")
        }
    }

    private fun getExtras() {
        if (intent.action == null) {
            idMovie = intent.getIntExtra(Constant.FILME_ID, 0)
            color = intent.getIntExtra(Constant.COLOR_TOP, R.color.transparent)
        } else {
            idMovie = Integer.parseInt(intent.getStringExtra(Constant.FILME_ID))
            color = Integer.parseInt(intent.getStringExtra(Constant.COLOR_TOP))
        }
    }

    private fun setFAB() {
        if (mAuth?.currentUser != null) {

            setEventListenerFavorite()
            setEventListenerRated()
            setEventListenerWatch()

            fab_menu?.alpha = 1.0f
            setColorFab(color)
            menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
            menu_item_rated?.setOnClickListener(ratedFilme())
            menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())
        } else {
            fab_menu?.alpha = 0.0f
        }
    }

    private fun snack() {
        Snackbar.make(top_img_viewpager, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    getDados()
                } else {
                    snack()
                }
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.share) {
            if (movieDb != null) {

                salvaImagemMemoriaCache(this@MovieDetailsActivity, movieDb?.posterPath, object : SalvarImageShare {
                    override fun retornaFile(file: File) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "message/rfc822"
                        intent.putExtra(Intent.EXTRA_TEXT, movieDb?.title + " " + buildDeepLink() + " by: " + Constant.TWITTER_URL)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(this@MovieDetailsActivity, file))
                        startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar) + " " + movieDb?.title))
                    }

                    override fun RetornoFalha() {
                        Toast.makeText(this@MovieDetailsActivity, resources.getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@MovieDetailsActivity, resources.getString(R.string.erro_ainda_sem_imagem), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun buildDeepLink(): String {
        // Get the unique appcode for this app.
        return "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3Dmovie%26id%3D${movieDb?.id}&apn=br.com.icaro.filme"
    }

    private fun ratedFilme(): View.OnClickListener {
        return View.OnClickListener {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                movieDb?.releaseDate?.let { date = sdf.parse(movieDb?.releaseDate) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_nao_lancado), Toast.LENGTH_SHORT).show()
            } else {
                val alertDialog = Dialog(this@MovieDetailsActivity)
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.setContentView(R.layout.dialog_custom_rated)

                val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
                val no = alertDialog.findViewById<View>(R.id.cancel_rated) as Button
                val title = alertDialog.findViewById<View>(R.id.rating_title) as TextView
                title.text = movieDb?.title
                val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
                ratingBar.rating = (numberRated / 2)

                alertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                alertDialog.show()

                if (addRated) {
                    no.visibility = View.VISIBLE
                } else {
                    no.visibility = View.GONE
                }

                no.setOnClickListener {

                    myRated?.child(idMovie.toString())?.setValue(null)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@MovieDetailsActivity,
                                resources.getText(R.string.remover_rated), Toast.LENGTH_SHORT).show()
                        }
                    alertDialog.dismiss()
                    fab_menu.close(true)
                }

                ok.setOnClickListener(View.OnClickListener {

                    if (UtilsApp.isNetWorkAvailable(this@MovieDetailsActivity)) {

                        if (ratingBar.rating == 0.0f) {
                            alertDialog.dismiss()
                            return@OnClickListener
                        }

                        val filmeDB = FilmeDB()
                        filmeDB.id = movieDb?.id!!
                        filmeDB.idImdb = movieDb?.imdbId
                        filmeDB.title = movieDb?.title
                        filmeDB.nota = ratingBar.rating * 2
                        filmeDB.poster = movieDb?.posterPath

                        myRated?.child(idMovie.toString())?.setValue(filmeDB)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@MovieDetailsActivity, resources.getString(R.string.filme_rated) + " - " + ratingBar.rating * 2, Toast.LENGTH_SHORT)
                                    .show()

                                fab_menu?.close(true)
                            }
                        Thread(Runnable { FilmeService.ratedMovieGuest(idMovie, (ratingBar.rating * 2).toInt(), this@MovieDetailsActivity) }).start()
                    }
                    alertDialog.dismiss()
                })
            }
        }
    }

    private fun setColorFab(color: Int) {
        fab_menu?.menuButtonColorNormal = color
        menu_item_favorite?.colorNormal = color
        menu_item_watchlist?.colorNormal = color
        menu_item_rated?.colorNormal = color
    }

    private fun addOrRemoveFavorite(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.2f)
            val anim3 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f, 1.0f)
            val animator = ObjectAnimator
                .ofPropertyValuesHolder(menu_item_favorite, anim1, anim2, anim3, anim4)
            animator.duration = 1600
            animator.start()

            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                movieDb?.releaseDate?.let { date = sdf.parse(movieDb?.releaseDate) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@MovieDetailsActivity, R.string.filme_nao_lancado, Toast.LENGTH_SHORT).show()
            } else {

                if (addFavorite) {
                    //  Log.d(TAG, "Apagou Favorite");
                    myFavorite?.child(idMovie.toString())?.setValue(null)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show()

                            fab_menu?.close(true)
                        }
                } else {

                    val filmeDB = FilmeDB()
                    filmeDB.id = movieDb?.id!!
                    filmeDB.idImdb = movieDb?.imdbId
                    filmeDB.title = movieDb?.title
                    filmeDB.poster = movieDb?.posterPath

                    myFavorite?.child(idMovie.toString())?.setValue(filmeDB)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                .show()

                            fab_menu?.close(true)
                        }
                }
            }
        }
    }

    private fun addOrRemoveWatch(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f)
            val anim2 = PropertyValuesHolder.ofFloat("scaley", 1.0f, 0.0f)
            val anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat("scaley", 0.5f, 1.0f)
            val animator = ObjectAnimator
                .ofPropertyValuesHolder(menu_item_watchlist, anim1, anim2, anim3, anim4)
            animator.duration = 1650
            animator.start()

            if (addWatch) {

                myWatch?.child(idMovie.toString())?.setValue(null)
                    ?.addOnCompleteListener {
                        Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_remove), Toast.LENGTH_SHORT).show()

                        fab_menu?.close(true)
                    }
            } else {

                val filmeDB = FilmeDB()
                filmeDB.idImdb = movieDb?.imdbId
                filmeDB.id = movieDb?.id!!
                filmeDB.title = movieDb?.title
                filmeDB.poster = movieDb?.posterPath

                myWatch?.child(idMovie.toString())?.setValue(filmeDB)
                    ?.addOnCompleteListener {
                        Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                            .show()

                        fab_menu?.close(true)
                    }
            }
        }
    }

    private fun setTitleAndDisableTalk(title: String) {
        val collapsingToolbarLayout = findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbarLayout.title = title
        collapsingToolbarLayout.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    private fun setFragmentInfo() {


        movieFragment = MovieFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Constant.FILME, movieDb)
                putInt(Constant.COLOR_TOP, color)
            }
        }
        if (!isDestroyed && !isFinishing) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.filme_container, movieFragment, null)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (valueEventWatch != null) {
            myWatch?.removeEventListener(valueEventWatch!!)
        }
        if (valueEventRated != null) {
            myRated?.removeEventListener(valueEventRated!!)
        }
        if (valueEventFavorite != null) {
            myFavorite?.removeEventListener(valueEventFavorite!!)
        }

        subscriptions.unsubscribe()
    }

    private inner class ImagemTopFragment(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return if (movieDb?.images?.backdrops != null) {
                if (position == 0) {
                    ImagemTopFilmeScrollFragment.newInstance(movieDb?.backdropPath)
                } else ImagemTopFilmeScrollFragment.newInstance(movieDb?.images?.backdrops!![position]?.filePath)
            } else Fragment()
        }

        override fun getCount(): Int {
            if (movieDb?.images?.backdrops != null) {
                val size = movieDb?.images?.backdrops?.size!!
                return if (size > 0) size else 1
            }
            return 0
        }
    }
}
