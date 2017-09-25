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
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.*
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.API
import domain.FilmeService
import domain.TvshowDB
import domain.UserTvshow
import domain.tvshow.Tvshow
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.tvserie_activity.*
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TvShowActivity : BaseActivity() {

    private val TAG = TvShowActivity::class.java.name

    private var id_tvshow: Int = 0
    private var color_top: Int = 0
    private lateinit var series: Tvshow
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var addFavorite = true
    private var addWatch = true
    private var addRated = true
    private var seguindo: Boolean = false
    private lateinit var valueEventWatch: ValueEventListener
    private lateinit var valueEventRated: ValueEventListener
    private lateinit var valueEventFavorite: ValueEventListener

    private var mAuth: FirebaseAuth? = null
    private var myFavorite: DatabaseReference? = null
    private var myWatch: DatabaseReference? = null
    private var myRated: DatabaseReference? = null
    private var numero_rated: Float = 0.0f
    private var database: FirebaseDatabase? = null
    private val userTvshow: UserTvshow? = null
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
    }

    private fun iniciarFirebases() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
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
            FirebaseCrash.report(e)
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
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                        startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar_filme)))

                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_MainActivity:menu_drav_home")
                        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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
                // Log.d(TAG, "Apagou Watch");
                myWatch?.child(series.id.toString())?.setValue(null)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_watch_remove), Toast.LENGTH_SHORT)
                                    .show()
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove))
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series?.name)
                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.id!!)
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                        }

            } else {
                // Log.d(TAG, "Gravou Watch");

                val tvshowDB = TvshowDB()
                tvshowDB.externalIds = series?.external_ids
                tvshowDB.title = series!!.name
                tvshowDB.id = series!!.id!!
                tvshowDB.poster = series!!.posterPath
                //tvshowDB.getExternalIds().setId(series.getId());

                myWatch!!.child(series!!.id.toString()).setValue(tvshowDB)
                        .addOnCompleteListener {
                            Toast.makeText(this@TvShowActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                    .show()
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_watchlist))
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series!!.name)
                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series!!.id!!)
                            firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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
                val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this@TvShowActivity)
                Toast.makeText(this@TvShowActivity, R.string.tvshow_nao_lancado, Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Favorite - Tvshow ainda não foi lançado.")
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            } else {

                if (addFavorite) {
                    myFavorite!!.child(id_tvshow.toString()).setValue(null)
                            .addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_remove_favorite), Toast.LENGTH_SHORT).show()
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.tvshow_remove_favorite))
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series!!.name)
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series!!.id!!)
                                firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                            }

                } else {

                    val tvshowDB = TvshowDB()
                    tvshowDB.externalIds = series!!.external_ids
                    tvshowDB.title = series!!.name
                    tvshowDB.id = series!!.id!!
                    tvshowDB.poster = series!!.posterPath
                    // tvshowDB.getExternalIds().setId(series.getId());

                    myFavorite!!.child(id_tvshow.toString()).setValue(tvshowDB)
                            .addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_add_favorite), Toast.LENGTH_SHORT)
                                        .show()
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.tvshow_add_favorite))
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series!!.name)
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series!!.id!!)
                                firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Tentativa de Rated fora da data de lançamento")
                firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                Toast.makeText(this@TvShowActivity, getString(R.string.tvshow_nao_lancado), Toast.LENGTH_SHORT).show()

            } else {

                val alertDialog = Dialog(this@TvShowActivity)
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.setContentView(R.layout.adialog_custom_rated)

                val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
                val no = alertDialog.findViewById<View>(R.id.cancel_rated) as Button


                val title = alertDialog.findViewById<View>(R.id.rating_title) as TextView
                title.text = series!!.name
                val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
                ratingBar.rating = numero_rated
                val width = resources.getDimensionPixelSize(R.dimen.popup_width)
                val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

                alertDialog.window!!.setLayout(width, height)

                if (addRated) {
                    no.visibility = View.VISIBLE
                } else {
                    no.visibility = View.GONE
                }

                no.setOnClickListener {
                    //    Log.d(TAG, "Apagou Rated");
                    myRated!!.child(id_tvshow.toString()).setValue(null)
                            .addOnCompleteListener {
                                Toast.makeText(this@TvShowActivity,
                                        resources.getText(R.string.tvshow_remove_rated), Toast.LENGTH_SHORT).show()
                            }
                    alertDialog.dismiss()
                    fab_menu_filme?.close(true)
                }

                ok.setOnClickListener {
                    //  Log.d(TAG, "Adialog Rated");

                    val progressDialog = ProgressDialog(this@TvShowActivity,
                            android.R.style.Theme_Material_Dialog)
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage(resources.getString(R.string.salvando))
                    progressDialog.show()

                    if (UtilsApp.isNetWorkAvailable(this@TvShowActivity)) {

                        //  Log.d(TAG, "Gravou Rated");

                        val tvshowDB = TvshowDB()
                        tvshowDB.externalIds = series!!.external_ids
                        tvshowDB.nota = ratingBar.rating.toInt()
                        tvshowDB.id = series!!.id!!
                        tvshowDB.title = series!!.name
                        tvshowDB.poster = series!!.posterPath
                        // tvshowDB.getExternalIds().setId(series.getId());

                        myRated?.child(id_tvshow.toString())?.setValue(tvshowDB)
                                ?.addOnCompleteListener {
                                    Toast.makeText(this@TvShowActivity,
                                            getString(R.string.tvshow_rated), Toast.LENGTH_SHORT)
                                            .show()
                                    val bundle = Bundle()
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,
                                            getString(R.string.tvshow_rated))
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series!!.name)
                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series!!.id!!)
                                    firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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

    }

    private fun setImageTop() {

        Picasso.with(this@TvShowActivity)
                .load(UtilsApp.getBaseUrlImagem(5)!! + series!!.backdropPath!!)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
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


    //    private void atualizarRealDate() {
    //        try {
    //            new Thread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    TmdbTvSeasons tvSeasons = new TmdbApi(Config.TMDB_API_KEY).getTvSeasons();
    //
    //                    userTvshow = setUserTvShow(series);
    //
    //                    for (int i = 0; i < series.getSeasons().size(); i++) {
    //                        TvSeason tvS = series.getSeasons().get(i);
    //                        TvSeason tvSeason = tvSeasons.getSeason(series.getId(), tvS.getSeasonNumber(), "en", TmdbTvSeasons.SeasonMethod.external_ids); //?
    //                        userTvshow.getSeasons().get(i).setUserEps(setEp(tvSeason));
    //                        // Atualiza os eps em userTvShow
    //                    }
    //
    //                    for (int i = 0; i < userTvshowOld.getSeasons().size(); i++) {
    //                        userTvshow.getSeasons().get(i).setId(userTvshowOld.getSeasons().get(i).getId());
    //                        userTvshow.getSeasons().get(i).setSeasonNumber(userTvshowOld.getSeasons().get(i).getSeasonNumber());
    //                        userTvshow.getSeasons().get(i).setVisto(userTvshowOld.getSeasons().get(i).isVisto());
    //                        //Atualiza somente os campos do temporada em userTvShow
    //                    }
    //
    //                    for (int i = 0; i < userTvshowOld.getSeasons().size(); i++) {
    //                      //  Log.d(TAG, "Numero de eps - "+ userTvshow.getSeasons().get(i).getUserEps().size());
    //                        if (userTvshow.getSeasons().get(i).getUserEps().size() > userTvshowOld.getSeasons().get(i).getUserEps().size()) {
    //                            userTvshow.getSeasons().get(i).setVisto(false);
    //                            // Se huver novos ep. coloca temporada com não 'vista'
    //                        }
    //                            for (int i1 = 0; i1 < userTvshowOld.getSeasons().get(i).getUserEps().size(); i1++) {
    //                                if (i1 < userTvshow.getSeasons().get(i).getUserEps().size())
    //                                userTvshow.getSeasons().get(i).getUserEps().set(i1, userTvshowOld.getSeasons().get(i).getUserEps().get(i1));
    //                              //  Log.d(TAG, "run: EPS "+ i1);
    //                                //coloca as informações antigas na nova versão dos dados.
    //                            }
    //                    }
    //
    //                    final DatabaseReference myRef = database.getReference("users");
    //                    myRef.child(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "")
    //                            .child("seguindo")
    //                            .child(String.valueOf(series.getId()))
    //                            .setValue(userTvshow)
    //                            .addOnCompleteListener(new OnCompleteListener<Void>() {
    //                                @Override
    //                                public void onComplete(@NonNull Task<Void> task) {
    //                                    if (task.isSuccessful()) {
    //                                        seguindo = true;
    //                                        setupViewPagerTabs();
    //                                        setTitle();
    //                                        setImageTop();
    //                                        runOnUiThread(new Runnable() {
    //                                            @Override
    //                                            public void run() {
    //                                                Toast.makeText(TvShowActivity.this, R.string.season_updated, Toast.LENGTH_SHORT).show();
    //                                            }
    //                                        });
    ////
    //                                    }
    //                                }
    //                            });
    //                }
    //            }).start();
    //        } catch (Exception e){
    //            FirebaseCrash.report(e);
    //            runOnUiThread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    Toast.makeText(TvShowActivity.this, R.string.ops_seguir_novamente, Toast.LENGTH_SHORT).show();
    //                }
    //            });
    //        }
    //    }

    //    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
    //
    //        @Override
    //        protected Void doInBackground(Void... voids) {
    //            boolean idioma_padrao = false;
    //            try {
    //                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TvShowActivity.this);
    //                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
    //            } catch (Exception e){
    //                FirebaseCrash.report(e);
    //            }
    //
    //
    //            if (idioma_padrao) {
    //                try {
    //                    TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
    //                    series = tmdbTv
    //                            .getSeries(id_tvshow, getLocale()
    //                                    ,images, credits, videos, external_ids);
    //                    series.getVideos().addAll(tmdbTv.getSeries(id_tvshow, null, videos).getVideos());
    //                    series.getImages().setPosters(tmdbTv.getSeries(id_tvshow, null, images).getImages().getPosters());
    //                    // Log.d(TAG, String.valueOf(series.getNumberOfEpisodes()));
    //
    //                } catch (Exception e) {
    //                    // Log.d(TAG, e.getMessage());
    //                    FirebaseCrash.report(e);
    //                    if (!isFinishing())
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            Toast.makeText(TvShowActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
    //                        }
    //                    });
    //                }
    //
    //            } else {
    //                try {
    //                    series = FilmeService.getTmdbTvShow()
    //                            .getSeries(id_tvshow, null, images, credits, videos, external_ids);
    //                } catch (Exception e) {
    //                    FirebaseCrash.report(e);
    //                    if (!isFinishing())
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            Toast.makeText(TvShowActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
    //                        }
    //                    });
    //                }
    //            }
    //
    //            try {
    //                if (series.getFirstAirDate() != null) {
    //                    String date = series.getFirstAirDate().substring(0, 4);
    //                    netflix = FilmeService.getNetflix(series.getOriginalName(), Integer.parseInt(date));
    //                }
    //            } catch (Exception e) {
    //                FirebaseCrash.report(e);
    //            }
    //
    //
    //            try {
    //                if (series.getExternalIds().getImdbId() != null) {
    //                    imdbdb = FilmeService.getImdb(series.getExternalIds().getImdbId());
    //                }
    //            } catch (Exception e) {
    //                FirebaseCrash.report(e);
    //            }
    //
    //            return null;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(Void aVoid) {
    //            super.onPostExecute(aVoid);
    //            if (series == null) {
    //                return;
    //            }
    //
    //            if (mAuth.getCurrentUser() != null && series != null) {
    //                DatabaseReference myRef = database.getReference("users");
    //                myRef.child(mAuth.getCurrentUser().getUid()).child("seguindo").child(String.valueOf(series.getId()))
    //                        .addListenerForSingleValueEvent(
    //                                new ValueEventListener() {
    //                                    @Override
    //                                    public void onDataChange(DataSnapshot dataSnapshot) {
    //
    //                                        if (dataSnapshot.exists()) {
    //                                            userTvshowOld = dataSnapshot.getValue(UserTvshow.class);
    //
    //                                            if (userTvshowOld.getNumberOfEpisodes() == series.getNumberOfEpisodes()) {
    //                                                seguindo = true;
    //                                                setupViewPagerTabs();
    //                                                setTitle();
    //                                                setImageTop();
    //                                            } else {
    //                                                if (userTvshowOld.getNumberOfEpisodes() < series.getNumberOfEpisodes())
    //                                                atualizarRealDate();
    //                                            }
    //                                        } else {
    //                                            setupViewPagerTabs();
    //                                            setTitle();
    //                                            setImageTop();
    //                                        }
    //
    //                                    }
    //
    //                                    @Override
    //                                    public void onCancelled(DatabaseError databaseError) {
    //                                    }
    //                                });
    //            } else {
    //                seguindo = false;
    //                setTitle();
    //                setupViewPagerTabs();
    //                setImageTop();
    //            }
    //
    //            if (mAuth.getCurrentUser() != null) {
    //
    //                setEventListenerWatch();
    //                setEventListenerFavorite();
    //                setEventListenerRated();
    //
    //                Date date = null;
    //                fab_menu.setAlpha(1);
    //                if (series.getFirstAirDate() != null) {
    //                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    //                    try {
    //                        date = sdf.parse(series.getFirstAirDate());
    //                    } catch (ParseException e) {
    //                        e.printStackTrace();
    //                    }
    //                    if (UtilsApp.verificaLancamento(date)) {
    //                        menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
    //                        menu_item_rated.setOnClickListener(RatedFilme());
    //                    }
    //                    menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
    //                } else {
    //                    menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
    //                    menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
    //                    menu_item_rated.setOnClickListener(RatedFilme());
    //                }
    //            } else {
    //                fab_menu.setAlpha(0);
    //            }
    //        }
    //    }

    private fun setDados() {
        if (mAuth?.currentUser != null) {
            val myRef = database?.getReference("users")
            myRef?.child(mAuth?.currentUser?.uid)?.child("seguindo")?.child(series?.id.toString())
                    ?.addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        userTvshowOld = dataSnapshot.getValue(UserTvshow::class.java)

                                        if (userTvshowOld?.numberOfEpisodes == series?.numberOfEpisodes) {
                                            seguindo = true
                                            setupViewPagerTabs()
                                            setTitle()
                                            setImageTop()
                                        } else {
                                            if (userTvshowOld?.numberOfEpisodes!! < series?.numberOfEpisodes!!) {
                                                // atualizarRealDate();
                                            }
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
            if (series.firstAirDate != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    date = sdf.parse(series.firstAirDate)
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