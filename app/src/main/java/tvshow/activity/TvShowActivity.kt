package tvshow.activity

import activity.BaseActivityAb
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Loading
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.TvSeasons
import domain.TvshowDB
import domain.UserEp
import domain.UserSeasons
import domain.UserTvshow
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.Tvshow
import kotlinx.android.synthetic.main.fab_float.fab_menu
import kotlinx.android.synthetic.main.fab_float.menu_item_favorite
import kotlinx.android.synthetic.main.fab_float.menu_item_rated
import kotlinx.android.synthetic.main.fab_float.menu_item_watchlist
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import kotlinx.android.synthetic.main.tvserie_activity.collapsing_toolbar
import kotlinx.android.synthetic.main.tvserie_activity.img_top_tvshow
import kotlinx.android.synthetic.main.tvserie_activity.streamview_tv
import kotlinx.android.synthetic.main.tvserie_activity.tabLayout
import kotlinx.android.synthetic.main.tvserie_activity.toolbar
import kotlinx.android.synthetic.main.tvserie_activity.viewPager_tvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rx.schedulers.Schedulers
import tvshow.TvShowAdapter
import tvshow.viewmodel.TvShowViewModel
import utils.Api
import utils.Constant
import utils.UtilsApp
import utils.UtilsApp.setEp2
import utils.animeRotation
import utils.getNameTypeReel
import utils.gone
import utils.makeToast
import utils.setAnimation
import utils.setPicassoWithCache
import utils.visible
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TvShowActivity(override var layout: Int = R.layout.tvserie_activity) : BaseActivityAb() {

    private val EMPTY_RATED: Float = 0.0f
    var reelGood: ReelGoodTv? = null
    private lateinit var model: TvShowViewModel
    private var idTvshow: Int = 0
    private var idReel: String = ""
    private var colorTop: Int = 0
    private var series: Tvshow? = null

    private var seguindo: Boolean = false

    private var mAuth: FirebaseAuth? = null
    private var numberRated: Float = 0.0f
    private var database: FirebaseDatabase? = null
    private var userTvshow: UserTvshow? = null
    private var userTvshowOld: UserTvshow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolBar()
        setupNavDrawer()
        getExtras()
        setTitleAndDisableTalk()
        model = createViewModel(TvShowViewModel::class.java, this)
        observers()

        if (idReel.isNotEmpty()) getDateReel(idReel)

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            getDataTvshow()
        } else {
            snack()
        }
    }

    fun getModelView() = model

    private fun observers() {
        model.favorit.observe(this, Observer {
            setEventListenerFavorite(it.child("$idTvshow").exists())
        })

        model.rated.observe(this, Observer {
            setEventListenerRated(it.child("$idTvshow").exists())
            setRatedValue(it)
        })

        model.auth.observe(this, Observer {
            setFabVisible(it)
        })

        model.watch.observe(this, Observer {
            setEventListenerWatch(it.child("$idTvshow").exists())
        })

        model.tvshow.observe(this, Observer {
            when(it) {
                is Success -> {
                    series = it.result
                    setupViewPagerTabs(it.result)
                    setImageTop(it.result.backdropPath ?: "")
                    setFab()
                    atualizarRealDate()
                    model.loading(false)
                }
                is Failure -> { ops() }
                is Loading -> { setLoading(it.loading)}
            }
        })
    }

    private fun setLoading(loading: Boolean) {
        progress_horizontal.visibility = if (loading) View.GONE else View.GONE
    }

    private fun setFabVisible(visible: Boolean) {
        if (visible) {
            setColorFab(colorTop)
            fab_menu.visible()
        } else {
            fab_menu.gone()
        }
    }

    private fun getDateReel(idReel: String = getIdStream()) {
        GlobalScope.launch(Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, erro ->
            Handler(Looper.getMainLooper()).post {
                streamview_tv.error = true
                setAnimated()
            }
        }) {
            reelGood = withContext(Dispatchers.Default) {
                Api(this@TvShowActivity).getAvaliableShow(idReel)
            }
            streamview_tv.fillStream(series?.originalName?.getNameTypeReel()
                ?: "", reelGood?.sources!!)
            setAnimated()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setAnimated() {
        // Todo colocar dentro do Stream
        val sheet = BottomSheetBehavior.from(streamview_tv)
        (sheet as? BottomSheetBehavior<View>)?.setAnimation(viewPager_tvshow, streamview_tv.findViewById(R.id.title_streaming))
        streamview_tv.setClose(sheet)
    }

    private fun getIdStream(): String {
        return try {
            "${series?.originalName?.getNameTypeReel()
                ?: ""}-${series?.firstAirDate?.substring(0, 4)}"
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    private fun setTitleAndDisableTalk() {
        collapsing_toolbar?.setBackgroundColor(colorTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsing_toolbar.title = " "
        collapsing_toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        collapsing_toolbar.isFocusable = false
        toolbar.title = " "
        toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    private fun getDataTvshow() {
        GlobalScope.launch {
            model.getTvshow(idTvshow)
        }
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

    private fun setRatedValue(it: DataSnapshot) {
        numberRated = it.child(idTvshow.toString()).child("nota").value?.toString()?.toFloat()
            ?: EMPTY_RATED
    }

    override fun onDestroy() {
        model.destroy()
        super.onDestroy()
    }

    private fun getExtras() {
        if (intent.action == null) {
            colorTop = intent.getIntExtra(Constant.COLOR_TOP, R.color.colorFAB)
            idTvshow = intent.getIntExtra(Constant.TVSHOW_ID, 0)
            if (intent.hasExtra(Constant.ID_REEL)) {
                idReel = intent.getStringExtra(Constant.ID_REEL)
            }
        } else {
            colorTop = Integer.parseInt(intent.getStringExtra(Constant.COLOR_TOP))
            idTvshow = Integer.parseInt(intent.getStringExtra(Constant.TVSHOW_ID))
            if (intent.hasExtra(Constant.ID_REEL)) {
                idReel = intent.getStringExtra(Constant.ID_REEL)
            }
        }
    }

    private fun snack() {
        Snackbar.make(viewPager_tvshow, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    getDataTvshow()
                } else {
                    snack()
                }
            }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (series != null) {
            if (item.itemId == R.id.share) {
                salvaImagemMemoriaCache(this@TvShowActivity, series?.posterPath, object : SalvarImageShare {
                    override fun retornaFile(file: File) {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_TEXT, series?.name + " " + buildDeepLink() + " by: " + Constant.TWITTER_URL)
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(this@TvShowActivity, file))
                        }
                        startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar) + " " + series?.name))
                    }

                    override fun RetornoFalha() {
                        Toast.makeText(this@TvShowActivity, resources.getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(this@TvShowActivity, resources.getString(R.string.erro_ainda_sem_imagem), Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu) //
        return true
    }

    fun buildDeepLink(): String {
        // Get the unique appcode for this app.
        return "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3Dtvshow%26id%3D${series?.id}&apn=br.com.icaro.filme"
    }

    private fun makeTvshiwDb() = TvshowDB().apply {
        title = series?.name
        id = series?.id ?: 0
        poster = series?.posterPath
    }

    private fun addOrRemoveWatch() = View.OnClickListener {
        menu_item_watchlist.animeRotation()
        model.chanceWatch(add = {
            it.child("$idTvshow").setValue(makeTvshiwDb())
                .addOnCompleteListener {
                    makeToast(R.string.filme_add_watchlist)
                }
        }, remove = {
            it.child("$idTvshow").setValue(null)
                .addOnCompleteListener {
                    makeToast(R.string.tvshow_watch_remove)
                }
        }, idMedia = idTvshow)
        this@TvShowActivity.fab_menu.close(true)
    }

    private fun addOrRemoveFavorite() = View.OnClickListener {
        menu_item_favorite.animeRotation()
        if (!UtilsApp.verifyLaunch(getDateTvshow())) {
            makeToast(R.string.tvshow_nao_lancado)
        } else {
            model.putFavority(
                add = {
                    it.child(idTvshow.toString()).setValue(null)
                        .addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_remove_favorite), Toast.LENGTH_SHORT).show()
                        }
                },
                remove = {
                    it.child(idTvshow.toString())?.setValue(makeTvshiwDb())
                        .addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_add_favorite), Toast.LENGTH_SHORT)
                                .show()
                        }
                },
                id = idTvshow)

            this@TvShowActivity.fab_menu.close(true)
        }
    }

    private fun ratedMovie() = View.OnClickListener {
        if (!UtilsApp.verifyLaunch(getDateTvshow())) {
            Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_nao_lancado), Toast.LENGTH_SHORT).show()
        } else {
            Dialog(this@TvShowActivity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_custom_rated)
                findViewById<TextView>(R.id.rating_title).text = series?.name ?: ""
                window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

                val ratingBar = findViewById<RatingBar>(R.id.ratingBar_rated)
                ratingBar.rating = numberRated / 2

                findViewById<Button>(R.id.cancel_rated)
                    .setOnClickListener {
                        removeRated()
                        dismiss()
                    }

                findViewById<Button>(R.id.ok_rated).setOnClickListener {
                    if (ratingBar.rating == 0.0f) {
                        removeRated()
                    } else {
                        addRated(ratingBar)
                    }
                    dismiss()
                }
                show()
            }
        }
    }

    private fun addRated(ratingBar: RatingBar) {
        val tvshowDB = makeTvshiwDb().apply { nota = ratingBar.rating * 2 }
        model.setRated(idTvshow) { database ->
            database.child(idTvshow.toString()).setValue(tvshowDB)
                .addOnCompleteListener {
                    makeToast("${getString(R.string.tvshow_rated)} - ${tvshowDB.nota}")
                }
            GlobalScope.launch {
                model.setRatedOnTheMovieDB(tvshowDB)
            }
        }
        this@TvShowActivity.fab_menu.close(true)
    }

    private fun removeRated() {
        model.setRated(idTvshow) { database ->
            database.child(idTvshow.toString()).setValue(null)
                .addOnCompleteListener {
                    makeToast(string.tvshow_remove_rated)
                }
            this@TvShowActivity.fab_menu.close(true)
        }
    }

    private fun getDateTvshow(): Date? {
        return try {
            series?.firstAirDate?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.parse(it)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun setupViewPagerTabs(tvshow: Tvshow) {
        viewPager_tvshow?.offscreenPageLimit = 2
        viewPager_tvshow?.adapter = TvShowAdapter(this, supportFragmentManager, tvshow, colorTop, seguindo)
        viewPager_tvshow?.currentItem = 0
        tabLayout.setupWithViewPager(viewPager_tvshow)
        tabLayout.setSelectedTabIndicatorColor(colorTop)
    }

    private fun setImageTop(path: String) {
        img_top_tvshow.setPicassoWithCache(path, 5, {}, {}, R.drawable.top_empty )
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(img_top_tvshow, View.X, -100f, 0.0f)
                .setDuration(1000))
            start()
        }
    }

    private fun setColorFab(color: Int) {
        fab_menu?.menuButtonColorNormal = color
        menu_item_favorite?.colorNormal = color
        menu_item_watchlist?.colorNormal = color
        menu_item_rated?.colorNormal = color
    }

    fun atualizarRealDate() {

        userTvshow = series?.let { UtilsApp.setUserTvShow(it) }

        series?.seasons?.forEachIndexed { index, seasonsItem ->

            val subscriber = Api(this)
                .getTvSeasons(idTvshow, seasonsItem?.seasonNumber!!, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .onBackpressureBuffer(1000)
                .subscribe(object : rx.Observer<TvSeasons> {
                    override fun onCompleted() {
                        atulizarDataBase()
                        setDataBase()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@TvShowActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        Log.d("TAG", e.message)
                    }

                    override fun onNext(tvshow: TvSeasons) {
                        userTvshow?.seasons?.get(index)?.userEps = setEp2(tvshow)?.toMutableList()
                    }
                })

            //compositeSubscription?.add(subscriber)
        }
    }

    private fun atulizarDataBase() {

        userTvshowOld?.seasons?.forEachIndexed { index, _ ->

            if (userTvshow?.seasons?.get(index)?.id == userTvshowOld?.seasons?.get(index)?.id) {
                userTvshow?.seasons?.get(index)?.seasonNumber = userTvshowOld?.seasons?.get(index)?.seasonNumber!!
                userTvshow?.seasons?.get(index)?.isVisto = userTvshowOld?.seasons?.get(index)?.isVisto!!
            }

            atulizarDataBaseEps(index)
        }

        userTvshowOld?.seasons?.forEachIndexed { index: Int, userSeasons: UserSeasons? ->

            val epsSeasonTotal = userTvshow?.seasons?.get(index)?.userEps?.size ?: -1
            val epsSeasonTotalOld = userTvshowOld?.seasons?.get(index)?.userEps?.size ?: -2

            if (epsSeasonTotal > epsSeasonTotalOld) {
                userTvshow?.seasons?.get(index)?.isVisto = false
            }
        }
    }

    private fun atulizarDataBaseEps(indexSeason: Int) {

        userTvshowOld?.seasons?.get(indexSeason)?.userEps?.forEachIndexed { index: Int, userEp: UserEp ->
            userTvshow?.seasons?.get(indexSeason)?.userEps?.set(index, userEp)
        }
    }

    private fun setDataBase() {
        val myRef = database?.getReference("users")
        myRef?.child(mAuth?.currentUser?.uid!!)
            ?.child("seguindo")
            ?.child(series?.id.toString())
            ?.setValue(userTvshow)
            ?.addOnCompleteListener { task ->
                if (task.isComplete) {
                    seguindo = true
                  //  setupViewPagerTabs(it)
                   // setImageTop()
                }
            }

        val hashMap = HashMap<String, Any>()
        hashMap["desatualizada"] = false
        myRef?.child(mAuth?.currentUser?.uid!!)
            ?.child("seguindo")
            ?.child(series?.id.toString())
            ?.updateChildren(hashMap)
            ?.addOnCompleteListener {
                makeToast(R.string.season_updated)
            }
    }

    private fun setDados() {
        if (mAuth?.currentUser != null) {
            val myRef = database?.getReference("users")
            myRef?.child(mAuth?.currentUser?.uid!!)?.child("seguindo")?.child(series?.id.toString())
                ?.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                try {
                                    userTvshowOld = dataSnapshot.getValue(UserTvshow::class.java)

                                    if (userTvshowOld?.numberOfEpisodes == series?.numberOfEpisodes) {
                                        seguindo = true
                                        //setupViewPagerTabs(it)
                                        //setImageTop()
                                    } else {
                                        atualizarRealDate()
                                    }
                                } catch (e: Exception) {
                                //    setupViewPagerTabs(it)
                                  //  setImageTop()
                                    Toast.makeText(this@TvShowActivity, resources.getString(R.string
                                        .ops_seguir_novamente), Toast.LENGTH_LONG).show()
                                    Crashlytics.logException(e)
                                }
                            } else {
                                //setupViewPagerTabs(it)
                                //setImageTop()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        } else {
            seguindo = false
          //  setupViewPagerTabs(it)
           // setImageTop()
        }
    }

    private fun setFab() {
        menu_item_watchlist.setOnClickListener(addOrRemoveWatch())
        menu_item_favorite.setOnClickListener(addOrRemoveFavorite())
        menu_item_rated.setOnClickListener(ratedMovie())
    }
}
