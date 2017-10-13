package activity

import adapter.SeguindoAdapater
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import domain.API
import domain.TvSeasons
import domain.UserTvshow
import domain.tvshow.Tvshow
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.UtilsApp
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by icaro on 25/11/16.
 */
class SeguindoActivity : BaseActivity() {

    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var progressBar: ProgressBar? = null
    private var linearLayout: LinearLayout? = null

    private var seguindoDataBase: DatabaseReference? = null
    private var atualizarDatabase: FirebaseDatabase? = null
    private var database: FirebaseDatabase? = null
    private var eventListener: ValueEventListener? = null
    private var mAuth: FirebaseAuth? = null

    private var userTvshowFire: MutableList<UserTvshow>? = null
    private var userTvshowNovo: UserTvshow? = null

    private var compositeSubscription: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setTitle(R.string.seguindo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewPager = findViewById<View>(R.id.viewpage_usuario) as ViewPager
        tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        progressBar = findViewById<View>(R.id.progress) as ProgressBar
        linearLayout = findViewById<View>(R.id.linear_usuario_list) as LinearLayout


        if (UtilsApp.isNetWorkAvailable(this)) {
            iniciarFirebases()
            setEventListenerSeguindo()
        } else {
            snack()
        }
    }


    private fun iniciarFirebases() {

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        seguindoDataBase = database?.getReference("users")?.child(mAuth?.currentUser!!
                .uid)?.child("seguindo")
    }

    protected fun snack() {
        Snackbar.make(linearLayout!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        //text_elenco_no_internet.setVisibility(View.GONE);
                        iniciarFirebases()
                        setEventListenerSeguindo()
                    } else {
                        snack()
                    }
                }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupViewPagerTabs() {

        viewPager?.offscreenPageLimit = 1
        viewPager?.currentItem = 0
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.setSelectedTabIndicatorColor(resources.getColor(R.color.accent))
        viewPager?.adapter = SeguindoAdapater(this@SeguindoActivity, supportFragmentManager,
                userTvshowFire)
    }

    private fun setEventListenerSeguindo() {
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userTvshowFire = ArrayList()
                if (dataSnapshot.exists()) {
                    dataSnapshot.children
                            .asSequence()
                            .map { it.getValue(UserTvshow::class.java) }
                            .forEach { userTvshowFire?.add(it!!) }
                    veriricarSerie()
                }
                setupViewPagerTabs()
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        seguindoDataBase?.addListenerForSingleValueEvent(eventListener)
    }

    fun veriricarSerie() {

        userTvshowFire?.forEachIndexed { indexSerie, userTvshow ->
            var serie: Tvshow? = null
            val subscriber = API(this)
                    .getTvShow(userTvshow.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Tvshow> {
                        override fun onCompleted() {
                            if (serie?.numberOfEpisodes != userTvshow.numberOfEpisodes) {
                                userTvshowNovo = UtilsApp.setUserTvShow(serie)
                                userTvshow.seasons.forEachIndexed { indexSeason, userSeasons ->
                                    if (serie?.seasons!![indexSeason]?.episodeCount != userSeasons.userEps.size) {
                                        atualizarRealDate(indexSerie, indexSeason, serie, userTvshow)}
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            // Toast.makeText(this@SeguindoActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                            Log.d("TAG", e.message)
                        }

                        override fun onNext(tvshow: Tvshow) {
                            serie = tvshow
                        }
                    })
            compositeSubscription?.add(subscriber)
        }
    }

    fun atualizarRealDate(indexSerie: Int, indexSeason: Int, serie: Tvshow?, userTvshow: UserTvshow) {

        var tvseasonRetorno: TvSeasons? = null
        val subscriber = API(this)
                .getTvSeasons(userTvshow.id, userTvshow.numberOfSeasons)
                .debounce(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(object : Observer<TvSeasons> {
                    override fun onCompleted() {
                        userTvshowNovo?.seasons?.get(indexSeason)?.userEps = UtilsApp.setEp2(tvseasonRetorno)
                        atulizarDataBase(indexSerie, indexSeason)
                    }

                    override fun onError(e: Throwable) {
                        //Toast.makeText(this@SeguindoActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        Log.d("TAG", e.message)
                    }

                    override fun onNext(tvseason: TvSeasons) {
                        tvseasonRetorno = tvseason
                    }
                })

        compositeSubscription?.add(subscriber)
    }

    private fun atulizarDataBase(indexSerie: Int, indexSeason: Int) {

        userTvshowNovo?.seasons?.get(indexSeason)?.seasonNumber = userTvshowFire!![indexSerie].seasons[indexSeason].seasonNumber
        userTvshowNovo?.seasons?.get(indexSeason)?.isVisto = userTvshowFire!![indexSerie].seasons[indexSeason].isVisto
        atulizarDataBaseEps(indexSerie, indexSeason)

    }

    private fun atulizarDataBaseEps(indexSerie: Int, indexSeason: Int) {

        for ((indexEp, userEp) in userTvshowFire?.get(indexSerie)?.seasons?.get(indexSeason)?.userEps?.withIndex()!!) {
            userTvshowNovo?.seasons?.get(indexSeason)?.userEps?.set(indexEp, userEp)
        }

        if (userTvshowNovo?.seasons?.get(indexSeason)?.userEps?.size!! > userTvshowFire?.get(indexSerie)?.seasons?.get(indexSeason)!!.userEps.size) {
            userTvshowNovo?.seasons?.get(indexSeason)?.isVisto = false
        }

        setDataBase(userTvshowNovo, indexSerie, indexSeason)
    }

    private fun setDataBase(userTvshowNovo: UserTvshow?, indexSerie: Int, index: Int) {

        val childUpdates = HashMap<String, Any>().apply {
            put("/numberOfEpisodes", userTvshowNovo?.numberOfEpisodes!!)
            put("/numberOfSeasons", userTvshowNovo?.numberOfSeasons)
            put("/poster", userTvshowNovo.poster)
            put("seasons/${index}", userTvshowNovo.seasons!![index])
        }

        atualizarDatabase = FirebaseDatabase.getInstance()
        val myRef = atualizarDatabase?.getReference("users")
        myRef?.child(mAuth?.currentUser?.uid)
                ?.child("seguindo")
                ?.child(userTvshowNovo?.id.toString())
                ?.updateChildren(childUpdates)
                ?.addOnCompleteListener({ task ->
                    if (task.isComplete) {
                        setupViewPagerTabs()
                        Toast.makeText(this@SeguindoActivity, R.string.season_updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("TAG", task.exception.toString())
                    }
                })


    }

    override fun onDestroy() {
        super.onDestroy()
        if (eventListener != null) {
            seguindoDataBase?.removeEventListener(eventListener!!)
        }
        compositeSubscription?.unsubscribe()
    }
}
