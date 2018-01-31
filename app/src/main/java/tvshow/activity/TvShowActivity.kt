package tvshow.activity

import activity.BaseActivity
import adapter.TvShowAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.*
import domain.tvshow.Tvshow
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import kotlinx.android.synthetic.main.tvserie_activity.*
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp
import utils.UtilsApp.setEp2
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


class TvShowActivity : BaseActivity() {

    private var id_tvshow: Int = 0
    private var color_top: Int = 0
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
        setContentView(R.layout.tvserie_activity)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        setupNavDrawer()
        getExtras()
        collapsing_toolbar?.setBackgroundColor(color_top)
        collapsing_toolbar?.title = " "

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setColorFab(color_top)

        iniciarFirebases()
        compositeSubscription = CompositeSubscription()

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            getDadosTvshow()
        } else {
            snack()
        }
    }

    private fun getDadosTvshow() {

        val subscriber = API(this)
                .loadTvshowComVideo(id_tvshow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Tvshow> {
                    override fun onCompleted() {
                        setDados()
                        setFab()
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

                if (dataSnapshot.child(id_tvshow.toString()).exists()) {
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
        myWatch?.addValueEventListener(valueEventWatch)

    }

    private fun setEventListenerRated() {
        valueEventRated = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id_tvshow.toString()).exists()) {
                    addRated = true
                    if (dataSnapshot.child(id_tvshow.toString()).child("nota").exists()) {
                        val nota = dataSnapshot.child(id_tvshow.toString()).child("nota").value.toString()
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
        myRated?.addValueEventListener(valueEventRated)

    }

    private fun setEventListenerFavorite() {
        valueEventFavorite = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id_tvshow.toString()).exists()) {
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
        myFavorite?.addValueEventListener(valueEventFavorite)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (valueEventWatch != null) {
            myWatch?.removeEventListener(valueEventWatch)
        }
        if (valueEventRated != null) {
            myRated?.removeEventListener(valueEventRated)
        }
        if (valueEventFavorite != null) {
            myFavorite?.removeEventListener(valueEventFavorite)
        }

        compositeSubscription?.unsubscribe()
    }

    private fun iniciarFirebases() {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        if (mAuth?.currentUser != null) {
            myWatch = database?.getReference("users")?.child(mAuth?.currentUser?.uid)?.child("watch")?.child("tvshow")

            myFavorite = database?.getReference("users")?.child(mAuth?.currentUser?.uid)?.child("favorites")?.child("tvshow")

            myRated = database?.getReference("users")?.child(mAuth?.currentUser?.uid)?.child("rated")?.child("tvshow")
        }

    }

    private fun getExtras() {

        if (intent.action == null) {
            color_top = intent.getIntExtra(Constantes.COLOR_TOP, R.color.colorFAB)
            id_tvshow = intent.getIntExtra(Constantes.TVSHOW_ID, 0)

        } else {
            color_top = Integer.parseInt(intent.getStringExtra(Constantes.COLOR_TOP))
            id_tvshow = Integer.parseInt(intent.getStringExtra(Constantes.TVSHOW_ID))

        }
    }

    protected fun snack() {
        Snackbar.make(viewPager_tvshow, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        getDadosTvshow()
                    } else {
                        snack()
                    }
                }.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        try {
            menuInflater.inflate(R.menu.menu_share, menu)

            val searchView = menu.findItem(R.id.search).actionView as SearchView
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.queryHint = resources.getString(R.string.procurar)
            searchView.isEnabled = false

        } catch (e: Exception) {
            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show()
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (series != null) {
            if (item.itemId == R.id.share) {

                salvaImagemMemoriaCache(this@TvShowActivity, series?.posterPath, object : SalvarImageShare {
                    override fun retornaFile(file: File) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "message/rfc822"
                        intent.putExtra(Intent.EXTRA_TEXT, series?.name + " " + buildDeepLink() + " by: " + Constantes.TWITTER_URL)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM,  UtilsApp.getUriDownloadImage(this@TvShowActivity, file))
                        startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar_filme)))

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

    fun buildDeepLink(): String {
        // Get the unique appcode for this app.

        val link = "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3DTA%26id%3D" + series?.id + "&apn=br.com.icaro.filme"

        // If the deep link is used in an advertisement, this value must be set to 1.
        val isAd = false
        if (isAd) {
            // builder.appendQueryParameter("ad", "1");
        }


        return link
    }

    private fun setTitle() {
        collapsing_toolbar?.title = series?.name
    }

    private fun addOrRemoveWatch(): View.OnClickListener {
        return View.OnClickListener { view ->

            val anim1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat("scaley", 1.0f, 0.0f)
            val anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat("scaley", 0.5f, 1.0f)
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

                val tvshowDB = TvshowDB()
                //  tvshowDB.externalIds = series?.external_ids
                tvshowDB.title = series?.name
                tvshowDB.id = series?.id!!
                tvshowDB.poster = series?.posterPath
                //tvshowDB.getExternalIds().setId(series.getId());

                myWatch?.child(series?.id.toString())?.setValue(tvshowDB)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                    .show()

                        }
            }
            fab_menu_filme!!.close(true)
        }
    }

    private fun addOrRemoveFavorite(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat("scaley", 1.0f, 0.2f)
            val anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat("scaley", 0.0f, 1.0f)
            val animator = ObjectAnimator
                    .ofPropertyValuesHolder(menu_item_favorite, anim1, anim2, anim3, anim4)
            animator.duration = 1700
            animator.start()

            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                date = sdf.parse(series!!.firstAirDate)
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
                        myFavorite?.child(id_tvshow.toString())?.setValue(null)
                                ?.addOnCompleteListener {
                                    Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_remove_favorite), Toast.LENGTH_SHORT).show()
                                }
                    } catch (e: Exception) {

                    }

                } else {

                    val tvshowDB = TvshowDB()
                    // tvshowDB.externalIds = series?.external_ids
                    tvshowDB.title = series?.name
                    tvshowDB.id = series?.id!!
                    tvshowDB.poster = series?.posterPath

                    myFavorite?.child(id_tvshow.toString())?.setValue(tvshowDB)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_add_favorite), Toast.LENGTH_SHORT)
                                        .show()
                            }
                }

                fab_menu_filme?.close(true)
            }
        }
    }

    private fun RatedFilme(): View.OnClickListener {
        return View.OnClickListener {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                date = sdf.parse(series!!.firstAirDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_nao_lancado), Toast.LENGTH_SHORT).show()
            } else {

                val alertDialog = Dialog(this@TvShowActivity)
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.setContentView(R.layout.adialog_custom_rated)

                val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
                val no = alertDialog.findViewById<View>(R.id.cancel_rated) as Button


                val title = alertDialog.findViewById<View>(R.id.rating_title) as TextView
                title.text = series?.name
                val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
                ratingBar.rating = numero_rated
                val width = resources.getDimensionPixelSize(R.dimen.popup_width)
                val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

                alertDialog.window?.setLayout(width, height)

                if (addRated) {
                    no.visibility = View.VISIBLE
                } else {
                    no.visibility = View.GONE
                }

                no.setOnClickListener {
                    myRated?.child(id_tvshow.toString())?.setValue(null)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity,
                                        resources.getText(R.string.tvshow_remove_rated), Toast.LENGTH_SHORT).show()
                            }
                    alertDialog.dismiss()
                    fab_menu_filme?.close(true)
                }

                ok.setOnClickListener {

                    val progressDialog = ProgressDialog(this@TvShowActivity,
                            android.R.style.Theme_Dialog)
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage(resources.getString(R.string.salvando))
                    progressDialog.show()

                    if (UtilsApp.isNetWorkAvailable(this@TvShowActivity)) {

                        //  Log.d(TAG, "Gravou Rated");

                        val tvshowDB = TvshowDB()
                        //tvshowDB.externalIds = series?.external_ids
                        tvshowDB.nota = ratingBar.rating.toInt()
                        tvshowDB.id = series?.id!!
                        tvshowDB.title = series?.name
                        tvshowDB.poster = series?.posterPath

                        myRated?.child(id_tvshow.toString())?.setValue(tvshowDB)
                                ?.addOnCompleteListener {
                                    Toast.makeText(this@TvShowActivity,
                                            getString(R.string.tvshow_rated), Toast.LENGTH_SHORT)
                                            .show()

                                }

                        Thread(Runnable { FilmeService.ratedTvshowGuest(id_tvshow, ratingBar.rating.toInt(), this@TvShowActivity) }).start()

                    }
                    progressDialog.dismiss()
                    alertDialog.dismiss()
                    fab_menu_filme?.close(true)
                }
                alertDialog.show()
            }
        }
    }

    private fun setupViewPagerTabs() {
        viewPager_tvshow?.offscreenPageLimit = 1
        viewPager_tvshow?.adapter = TvShowAdapter(this, supportFragmentManager, series, color_top, seguindo)
        val tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        viewPager_tvshow?.currentItem = 0
        tabLayout.setupWithViewPager(viewPager_tvshow)
        tabLayout.setSelectedTabIndicatorColor(color_top)
        progress_horizontal.visibility = View.GONE
    }

    private fun setImageTop() {

        Picasso.with(this@TvShowActivity)
                .load(UtilsApp.getBaseUrlImagem(5) + series?.backdropPath)
                .error(R.drawable.top_empty)
                .into(img_top_tvshow)

        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(img_top_tvshow, "x", -100f, 0.0f)
                .setDuration(1000)
        animatorSet.playTogether(animator)
        animatorSet.start()
    }

    private fun setColorFab(color: Int) {
        fab_menu_filme?.menuButtonColorNormal = color
        menu_item_favorite?.colorNormal = color
        menu_item_watchlist?.colorNormal = color
        menu_item_rated?.colorNormal = color
    }


    fun atualizarRealDate() {

        userTvshow = UtilsApp.setUserTvShow(series)

        series?.seasons?.forEachIndexed { index, seasonsItem ->

            val subscriber = API(this)
                    .getTvSeasons(id_tvshow, seasonsItem?.seasonNumber!!)
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
                            userTvshow?.seasons?.get(index)?.userEps = setEp2(tvshow)
                        }
                    })

            compositeSubscription?.add(subscriber)

        }

    }

    private fun atulizarDataBase() {

        userTvshowOld?.seasons?.forEachIndexed { index, userSeasons ->

            if (userTvshow?.seasons?.get(index)?.id == userTvshowOld?.seasons?.get(index)?.id) {
                userTvshow?.seasons?.get(index)?.seasonNumber = userTvshowOld?.seasons?.get(index)?.seasonNumber!!
                userTvshow?.seasons?.get(index)?.isVisto = userTvshowOld?.seasons?.get(index)?.isVisto!!
            }

            atulizarDataBaseEps(index)
        }

        userTvshowOld?.seasons?.forEachIndexed { index: Int, userSeasons: UserSeasons? ->
            if (userTvshow?.seasons?.get(index)?.userEps?.size!! > userTvshowOld?.seasons?.get(index)?.userEps?.size!!) {
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
        myRef?.child(mAuth?.currentUser?.uid)
                ?.child("seguindo")
                ?.child(series?.id.toString())
                ?.setValue(userTvshow)
                ?.addOnCompleteListener({ task ->
                    if (task.isComplete) {
                        seguindo = true
                        setupViewPagerTabs()
                        setTitle()
                        setImageTop()
                        Toast.makeText(this@TvShowActivity, R.string.season_updated, Toast.LENGTH_SHORT).show();
                    }
                })
    }

    private fun setDados() {
        if (mAuth?.currentUser != null) {
            val myRef = database?.getReference("users")
            myRef?.child(mAuth?.currentUser?.uid)?.child("seguindo")?.child(series?.id.toString())
                    ?.addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        try {
                                            userTvshowOld = dataSnapshot.getValue(UserTvshow::class.java)

                                            if (userTvshowOld?.numberOfEpisodes == series?.numberOfEpisodes) {
                                                seguindo = true
                                                setupViewPagerTabs()
                                                setTitle()
                                                setImageTop()
                                            } else {
                                                if (userTvshowOld?.numberOfEpisodes != series?.numberOfEpisodes) {
                                                    atualizarRealDate()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            setupViewPagerTabs()
                                            setTitle()
                                            setImageTop()
                                            Toast.makeText(this@TvShowActivity, resources.getString(R.string
                                                    .ops_seguir_novamente), Toast.LENGTH_LONG).show()
                                            Crashlytics.logException(e)
                                        }
                                    } else {
                                        setupViewPagerTabs()
                                        setTitle()
                                        setImageTop()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
        } else {
            seguindo = false
            setTitle()
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
            fab_menu_filme?.alpha = 1.0f
            if (series?.firstAirDate != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    date = sdf.parse(series?.firstAirDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                if (UtilsApp.verificaLancamento(date)) {
                    menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
                    menu_item_rated?.setOnClickListener(RatedFilme())
                }
                menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())
            } else {
                menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())
                menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
                menu_item_rated?.setOnClickListener(RatedFilme())
            }
        } else {
            fab_menu_filme?.alpha = 0.0f
        }
    }
}