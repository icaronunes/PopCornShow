package tvshow.activity

import activity.BaseActivityAb
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
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
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import domain.FilmeService
import domain.TvSeasons
import domain.TvshowDB
import domain.UserEp
import domain.UserSeasons
import domain.UserTvshow
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
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import tvshow.TvShowAdapter
import utils.Api
import utils.Constant
import utils.UtilsApp
import utils.UtilsApp.setEp2
import utils.getNameTypeReel
import utils.gone
import utils.makeToast
import utils.setAnimation
import utils.visible
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TvShowActivity(override var layout: Int = R.layout.tvserie_activity) : BaseActivityAb() {

    private var idTvshow: Int = 0
    private var colorTop: Int = 0
    private var series: Tvshow? = null
    private var addFavorite = true
    private var addWatch = true
    private var addRated = true
    private var seguindo: Boolean = false
    private var valueEventWatch: ValueEventListener? = null
    private var valueEventRated: ValueEventListener? = null
    private var valueEventFavorite: ValueEventListener? = null

    private var mAuth: FirebaseAuth? = null
    private var myFavorite: DatabaseReference? = null
    private var myWatch: DatabaseReference? = null
    private var myRated: DatabaseReference? = null
    private var numero_rated: Float = 0.0f
    private var database: FirebaseDatabase? = null
    private var userTvshow: UserTvshow? = null
    private var userTvshowOld: UserTvshow? = null
    private var compositeSubscription: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolBar()
        setupNavDrawer()
        getExtras()
        collapsing_toolbar?.setBackgroundColor(colorTop)
        setTitleAndDisableTalk()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setColorFab(colorTop)

        iniciarFirebases()
        compositeSubscription = CompositeSubscription()

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            getDadosTvshow()
        } else {
            snack()
        }
    }

    private fun getDateReel() {
        GlobalScope.launch(Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, erro ->
            Handler(Looper.getMainLooper()).post {
                Log.d(this.javaClass.name, erro.message!!)
                streamview_tv.error = true
                setAnimated()
            }
        }) {
            val reelGood = withContext(Dispatchers.IO) {
                Api(this@TvShowActivity).getAvaliableShow(getIdStream())
            }
            streamview_tv.fillStream(series?.name ?: "", reelGood.sources)
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
        collapsing_toolbar.title = " "
        collapsing_toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        collapsing_toolbar.isFocusable = false
        toolbar.title = " "
        toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    private fun getDadosTvshow() {
        val subscriber = Api(this)
            .loadTvshowComVideo(idTvshow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Tvshow> {
                override fun onCompleted() {
                    setDados()
                    setFab()
                    getDateReel()
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(this@TvShowActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                }

                override fun onNext(tvshow: Tvshow) {
                    series = tvshow
                }
            })

        compositeSubscription?.add(subscriber)
    }

    private fun setEventListenerWatch() {

        valueEventWatch = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(idTvshow.toString()).exists()) {
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
                if (dataSnapshot.child(idTvshow.toString()).exists()) {
                    addRated = true
                    if (dataSnapshot.child(idTvshow.toString()).child("nota").exists()) {
                        val nota = dataSnapshot.child(idTvshow.toString()).child("nota").value.toString()
                        numero_rated = java.lang.Float.parseFloat(nota)
                        menu_item_rated?.labelText = resources.getString(R.string.remover_rated)
                        if (numero_rated == 0f) {
                            menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
                        }
                    }
                } else {
                    addRated = false
                    numero_rated = 0f
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
                if (dataSnapshot.child(idTvshow.toString()).exists()) {
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

        compositeSubscription?.unsubscribe()
    }

    private fun iniciarFirebases() {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        if (mAuth?.currentUser != null) {
            myWatch = database?.getReference("users")?.child(mAuth?.currentUser?.uid!!)
                ?.child("watch")?.child("tvshow")

            myFavorite = database?.getReference("users")?.child(mAuth?.currentUser?.uid!!)
                ?.child("favorites")?.child("tvshow")

            myRated = database?.getReference("users")?.child(mAuth?.currentUser?.uid!!)
                ?.child("rated")?.child("tvshow")
        }
    }

    private fun getExtras() {

        if (intent.action == null) {
            colorTop = intent.getIntExtra(Constant.COLOR_TOP, R.color.colorFAB)
            idTvshow = intent.getIntExtra(Constant.TVSHOW_ID, 0)
        } else {
            colorTop = Integer.parseInt(intent.getStringExtra(Constant.COLOR_TOP))
            idTvshow = Integer.parseInt(intent.getStringExtra(Constant.TVSHOW_ID))
        }
    }

    private fun snack() {
        Snackbar.make(viewPager_tvshow, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    getDadosTvshow()
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
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "message/rfc822"
                        intent.putExtra(Intent.EXTRA_TEXT, series?.name + " " + buildDeepLink() + " by: " + Constant.TWITTER_URL)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(this@TvShowActivity, file))
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

    private fun addOrRemoveWatch(): View.OnClickListener {
        return View.OnClickListener {

            val anim1 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.0f)
            val anim3 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1.0f)
            val animator = ObjectAnimator
                .ofPropertyValuesHolder(menu_item_watchlist, anim1, anim2, anim3, anim4)
            animator.duration = 1700
            animator.start()

            if (addWatch) {

                myWatch?.child(series?.id.toString())?.setValue(null)
                    ?.addOnCompleteListener {
                        Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_watch_remove), Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {

                val tvshowDB = TvshowDB().apply {
                   title = series?.name
                    id = series?.id!!
                    poster = series?.posterPath
                }

                myWatch?.child(series?.id.toString())?.setValue(tvshowDB)
                    ?.addOnCompleteListener {
                        Toast.makeText(this@TvShowActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            fab_menu!!.close(true)
        }
    }

    private fun addOrRemoveFavorite(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.2f)
            val anim3 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f, 1.0f)
            val animator = ObjectAnimator
                .ofPropertyValuesHolder(menu_item_favorite, anim1, anim2, anim3, anim4)
            animator.duration = 1700
            animator.start()

            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                series?.firstAirDate?.let { date = sdf.parse(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@TvShowActivity, R.string.tvshow_nao_lancado, Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Favorite - Tvshow ainda não foi lançado.")
            } else {

                if (addFavorite) {
                    try {
                        myFavorite?.child(idTvshow.toString())?.setValue(null)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_remove_favorite), Toast.LENGTH_SHORT).show()
                            }
                    } catch (e: Exception) {
                    }
                } else {

                    val tvshowDB = TvshowDB()
                    tvshowDB.title = series?.name
                    tvshowDB.id = series?.id!!
                    tvshowDB.poster = series?.posterPath

                    myFavorite?.child(idTvshow.toString())?.setValue(tvshowDB)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_add_favorite), Toast.LENGTH_SHORT)
                                .show()
                        }
                }

                fab_menu?.close(true)
            }
        }
    }

    private fun ratedMovie(): View.OnClickListener {
        return View.OnClickListener {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                series?.firstAirDate?.let { date = sdf.parse(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_nao_lancado), Toast.LENGTH_SHORT).show()
            } else {

                val alertDialog = Dialog(this@TvShowActivity)
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.setContentView(R.layout.dialog_custom_rated)

                val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
                val no = alertDialog.findViewById<View>(R.id.cancel_rated) as Button

                val title = alertDialog.findViewById<View>(R.id.rating_title) as TextView
                title.text = series?.name
                val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
                ratingBar.rating = numero_rated / 2

                alertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

                if (addRated) {
                    no.visible()
                } else {
                    no.gone()
                }

                no.setOnClickListener {
                    myRated?.child(idTvshow.toString())?.setValue(null)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity,
                                resources.getText(R.string.tvshow_remove_rated), Toast.LENGTH_SHORT).show()
                        }
                    alertDialog.dismiss()
                    fab_menu?.close(true)
                }

                ok.setOnClickListener {

                    if (UtilsApp.isNetWorkAvailable(this@TvShowActivity)) {

                        //  Log.d(TAG, "Gravou Rated");

                        val tvshowDB = TvshowDB()
                        tvshowDB.nota = ratingBar.rating * 2
                        tvshowDB.id = series?.id!!
                        tvshowDB.title = series?.name
                        tvshowDB.poster = series?.posterPath

                        myRated?.child(idTvshow.toString())?.setValue(tvshowDB)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity,
                                    getString(R.string.tvshow_rated) + " - ${tvshowDB.nota}", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        Thread(Runnable { FilmeService.ratedTvshowGuest(idTvshow, (ratingBar.rating * 2).toInt(), this@TvShowActivity) })
                            .start()
                    }
                    alertDialog.dismiss()
                    fab_menu?.close(true)
                }
                alertDialog.show()
            }
        }
    }

    private fun setupViewPagerTabs() {
        viewPager_tvshow?.offscreenPageLimit = 1
        viewPager_tvshow?.adapter = TvShowAdapter(this, supportFragmentManager, series, colorTop, seguindo)
        viewPager_tvshow?.currentItem = 0
        tabLayout.setupWithViewPager(viewPager_tvshow)
        tabLayout.setSelectedTabIndicatorColor(colorTop)
        progress_horizontal.visibility = View.GONE
    }

    private fun setImageTop() {

        Picasso.get()
            .load(UtilsApp.getBaseUrlImagem(5) + series?.backdropPath)
            .error(R.drawable.top_empty)
            .into(img_top_tvshow)

        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(img_top_tvshow, View.X, -100f, 0.0f)
            .setDuration(1000)
        animatorSet.playTogether(animator)
        animatorSet.start()
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
                .subscribe(object : Observer<TvSeasons> {
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

            compositeSubscription?.add(subscriber)
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
                    setupViewPagerTabs()
                    setImageTop()
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
                                        setupViewPagerTabs()
                                        setImageTop()
                                    } else {
                                        atualizarRealDate()
                                    }
                                } catch (e: Exception) {
                                    setupViewPagerTabs()
                                    setImageTop()
                                    Toast.makeText(this@TvShowActivity, resources.getString(R.string
                                        .ops_seguir_novamente), Toast.LENGTH_LONG).show()
                                    Crashlytics.logException(e)
                                }
                            } else {
                                setupViewPagerTabs()
                                setImageTop()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        } else {
            seguindo = false
            setupViewPagerTabs()
            setImageTop()
        }
    }

    private fun setFab() {
        if (mAuth?.currentUser != null) {

            setEventListenerWatch()
            setEventListenerFavorite()
            setEventListenerRated()

            var date: Date? = null
            fab_menu?.alpha = 1.0f
            if (series?.firstAirDate != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    series?.firstAirDate.let { date = sdf.parse(it) }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                if (UtilsApp.verificaLancamento(date)) {
                    menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
                    menu_item_rated?.setOnClickListener(ratedMovie())
                }
                menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())
            } else {
                menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())
                menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
                menu_item_rated?.setOnClickListener(ratedMovie())
            }
        } else {
            fab_menu?.alpha = 0.0f
        }
    }
}
