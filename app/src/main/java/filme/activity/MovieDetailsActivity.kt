package filme.activity

import activity.BaseActivityAb
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
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Loading
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import domain.Movie
import domain.MovieDb
import filme.MovieDetatilsViewModel
import filme.fragment.MovieFragment
import fragment.ImagemTopFilmeScrollFragment
import kotlinx.android.synthetic.main.activity_movie.collapsing_toolbar
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.Api
import utils.Constant
import utils.UtilsApp
import utils.animeRotation
import utils.getNameTypeReel
import utils.gone
import utils.makeToast
import utils.setAnimation
import utils.visible
import utils.yearDate
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovieDetailsActivity(override var layout: Int = R.layout.activity_movie) : BaseActivityAb() {
    private val EMPTY_RATED = 0.0f
    private var numberRated: Float = EMPTY_RATED
    private lateinit var model: MovieDetatilsViewModel

    private var color: Int = 0
    private var idMovie: Int = 0
    private var movieDb: Movie? = null
    private var addRated = true


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = createViewModel(MovieDetatilsViewModel::class.java, this)
        setUpToolBar()
        setupNavDrawer()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleAndDisableTalk()
        getExtras()
        setupViewPager()
        observers()

        if (UtilsApp.isNetWorkAvailable(this)) {
            getData()
        } else {
            snack()
        }
    }

    private fun observers() {
        model.watch.observe(this, Observer {
            setEventListenerWatch(it.child("$idMovie").exists())
        })

        model.rated.observe(this, Observer {
            setEventListenerRated(it.child("$idMovie").exists())
            setRatedValue(it)
        })

        model.favorit.observe(this, Observer {
            setEventListenerFavorite(it.child("$idMovie").exists())
        })

        model.auth.observe(this, Observer {
            setFAB(it)
        })

        model.movie.observe(this, Observer {
            when (it) {
                is Success -> {
                    fillData(it.result)
                    setLoading(false)
                }
                is Failure -> ops()
                is Loading -> setLoading(it.loading)
            }
        })
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

    private fun setColorFab(color: Int) {
        fab_menu?.menuButtonColorNormal = color
        menu_item_favorite?.colorNormal = color
        menu_item_watchlist?.colorNormal = color
        menu_item_rated?.colorNormal = color
    }

    private fun setupViewPager() {
        top_img_viewpager.apply {
            setBackgroundColor(color)
            offscreenPageLimit = 3
        }
    }

    private fun fillData(movie: Movie) {
        movieDb = movie
        title = movie.title
        setupTopFragment(movie)
        setFragmentInfo()
        setStream() // Chamar antes de detalhes se possivel
    }

    private fun setupTopFragment(movie: Movie) {
        top_img_viewpager?.adapter = ImagemTopFragment(supportFragmentManager, movie)
    }

    private fun setLoading(boolean: Boolean) {
        if (boolean) {
            progress_horizontal?.visible()
        } else {
            progress_horizontal?.gone()
        }
    }

    private fun setRatedValue(it: DataSnapshot) {
        numberRated = it.child(idMovie.toString()).child("nota").value?.toString()?.toFloat()
            ?: EMPTY_RATED
    }

    private fun getData() {
        model.setLoading(true)
        model.getDataMovie(idMovie)
    }

    private fun getIdStream(): String {
        return try {
            "${movieDb?.originalTitle?.getNameTypeReel()
                ?: ""}-${movieDb?.releaseDate?.yearDate()}"
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
            val reelGood = withContext(Dispatchers.IO) {
                Api(this@MovieDetailsActivity).getAvaliableMovie(getIdStream())
            }

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

    private fun setEventListenerWatch(boolean: Boolean) {
        if (boolean) {
            menu_item_watchlist?.labelText = resources.getString(R.string.remover_watch)
        } else {
            menu_item_watchlist?.labelText = resources.getString(R.string.adicionar_watch)
        }
    }

    private fun setEventListenerRated(boolean: Boolean) {
        if (boolean) {
            menu_item_rated?.labelText = resources.getString(R.string.remover_rated)
        } else {
            menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
        }
    }

    private fun setEventListenerFavorite(boolean: Boolean) {
        if (boolean) {
            menu_item_favorite?.labelText = resources.getString(R.string.remover_favorite)
        } else {
            menu_item_favorite?.labelText = resources.getString(R.string.adicionar_favorite)
        }
    }

    private fun setFAB(boolean: Boolean) {
        if (boolean) {
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
                    getData()
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
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_TEXT, movieDb?.title + " " + buildDeepLink() + " by: " + Constant.TWITTER_URL)
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(this@MovieDetailsActivity, file))
                        }
                        startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar) + " " + movieDb?.title))
                    }

                    override fun RetornoFalha() {
                        makeToast(R.string.erro_na_gravacao_imagem)
                    }
                })
            } else {
                makeToast(R.string.erro_ainda_sem_imagem)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun buildDeepLink(): String {
        // Get the unique appcode for this app.
        return "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3Dmovie%26id%3D${movieDb?.id}&apn=br.com.icaro.filme"
    }

    private fun ratedFilme() = View.OnClickListener {

        if (!UtilsApp.verifyLaunch(getDateMovie())) {
            makeToast(R.string.filme_nao_lancado)
        } else {
            openDialog().apply {
                findViewById<TextView>(R.id.rating_title).text = movieDb?.title ?: ""
                val ratingBar = findViewById<RatingBar>(R.id.ratingBar_rated).apply { rating = (numberRated / 2) }
                findViewById<Button>(R.id.ok_rated).apply {
                    setOnClickListener {
                            if (ratingBar.rating == 0.0f) {
                                removeRated()
                            } else {
                                addRated(ratingBar)
                            }
                        dismiss()
                    }
                }
                findViewById<Button>(R.id.cancel_rated).apply {
                    setOnClickListener {
                        removeRated()
                        dismiss()
                    }
                }
            }
        }
    }

    private fun removeRated() {
        model.changeRated({
            it.child(idMovie.toString()).setValue(null)
                .addOnCompleteListener {
                    makeToast(string.remover_rated)
                }
            this@MovieDetailsActivity.fab_menu.close(true)
        }, idMovie = idMovie)
    }

    private fun addRated(ratingBar: RatingBar) {
        val movieDate = makeMovieDb().apply { nota = ratingBar.rating * 2 }
        model.changeRated({ databaseReference ->
            databaseReference.child(idMovie.toString()).setValue(movieDate)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        makeToast(String.format(resources.getString(string.filme_rated), "${movieDate.nota}"))
                        this@MovieDetailsActivity.fab_menu.close(true)
                    }
                }
            model.setRatedOnTheMovieDB(movieDate)
        }, idMovie)
    }

    private fun openDialog() = Dialog(this@MovieDetailsActivity).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_custom_rated)
        window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        show()
    }

    private fun addOrRemoveFavorite(): View.OnClickListener {
        return View.OnClickListener {
            menu_item_favorite.animeRotation()
            if (!UtilsApp.verifyLaunch(getDateMovie())) {
                makeToast( R.string.filme_nao_lancado)
            } else {
                model.executeFavority({
                    it.child(idMovie.toString()).setValue(null)
                        .addOnCompleteListener {
                            makeToast(R.string.filme_remove_favorite)
                            this@MovieDetailsActivity.fab_menu.close(true)
                        }
                }, {
                    it.child(idMovie.toString()).setValue(makeMovieDb())
                        .addOnCompleteListener {
                            makeToast(R.string.filme_add_favorite)
                            this@MovieDetailsActivity.fab_menu.close(true)
                        }
                }, idMovie)
            }
        }
    }

    private fun makeMovieDb() = MovieDb().apply {
        id = movieDb?.id!!
        idImdb = movieDb?.imdbId
        title = movieDb?.title
        poster = movieDb?.posterPath
    }

    private fun getDateMovie(): Date? {
        return try {
            movieDb?.releaseDate?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.parse(movieDb?.releaseDate)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun addOrRemoveWatch(): View.OnClickListener {
        return View.OnClickListener {
            menu_item_watchlist.animeRotation()
            model.chanceWatch(remove = {
                it.child(idMovie.toString()).setValue(null)
                    .addOnCompleteListener {
                        Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_remove), Toast.LENGTH_SHORT).show()
                        this@MovieDetailsActivity.fab_menu.close(true)
                    }
            }, add = {
                it.child(idMovie.toString()).setValue(makeMovieDb())
                    .addOnCompleteListener {
                        Toast.makeText(this@MovieDetailsActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                            .show()

                        this@MovieDetailsActivity.fab_menu.close(true)
                    }
            },
                idMedia = idMovie)
        }
    }

    private fun setTitleAndDisableTalk() {
        collapsing_toolbar.apply {
            title = " "
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }

    private fun setFragmentInfo() {
        val movieFragment = MovieFragment().apply {
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
        model.destroy()
        subscriptions.unsubscribe()
        super.onDestroy()
    }

    private inner class ImagemTopFragment(supportFragmentManager: FragmentManager, val movie: Movie) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return if (movie.images?.backdrops != null) {
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

    fun getModelView() = model
}
