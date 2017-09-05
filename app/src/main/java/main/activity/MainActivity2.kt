package main.activity

import activity.BaseActivity
import activity.MainActivity
import activity.SettingsActivity
import adapter.MainAdapter
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.firebase.crash.FirebaseCrash
import com.viewpagerindicator.CirclePageIndicator
import domain.API
import domain.FilmeService
import domain.TopMain
import fragment.ViewPageMainTopFragment
import info.movito.themoviedbapi.TvResultsPage
import info.movito.themoviedbapi.model.core.MovieResultsPage
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.UtilsApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by icaro on 03/09/17.
 */

class MainActivity2 : BaseActivity() {
    private var viewPager_main: ViewPager? = null
    private var viewpage_top_main: ViewPager? = null
    private var tmdbTv: TvResultsPage? = null
    private var tmdbMovies: MovieResultsPage? = null
    private var tabLayout: TabLayout? = null
    private val multi = ArrayList<TopMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        setupNavDrawer()
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(" ")

        viewPager_main = findViewById(R.id.viewPager_main) as ViewPager
        viewpage_top_main = findViewById(R.id.viewpage_top_main) as ViewPager
        viewpage_top_main!!.offscreenPageLimit = 3


        if (UtilsApp.isNetWorkAvailable(this)) {
            getDados()
            //TMDVAsync().execute()
        } else {
            snack()
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPref.getBoolean("41", true)) {
            val dialog = AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_popcorn2)
                    .setTitle(R.string.novidades_title)
                    .setMessage(R.string.novidades_text)
                    .setPositiveButton(R.string.ok) { dialogInterface, i ->
                        val editor = sharedPref.edit()
                        editor.putBoolean("41", false)
                        editor.remove("39")

                        editor.apply()
                    }.create()
            dialog.show()
        }

    }

    private fun getDados() {
        val subAirToday = API().BuscaDeSeries(API.TIPOBUSCA.FILME.melhores)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({ lista ->
                        lista.totalResults
                },{ erro ->
                    Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                    Log.d(javaClass.simpleName, "Erro " + erro.message)
                })

    }


    protected fun snack() {
        Snackbar.make(viewpage_top_main!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        TMDVAsync().execute()
                    } else {
                        snack()
                    }
                }.show()
    }

    private fun setupViewPagerTabs() {

        viewPager_main!!.offscreenPageLimit = 2
        tabLayout = findViewById(R.id.tabLayout) as TabLayout
        viewPager_main!!.currentItem = 0
        viewPager_main!!.adapter = MainAdapter(this, supportFragmentManager)
        tabLayout!!.setupWithViewPager(viewPager_main)
        val circlePageIndicator = findViewById(R.id.indication_main) as CirclePageIndicator
        viewpage_top_main!!.adapter = ViewPageMainTopFragment(supportFragmentManager, multi)
        circlePageIndicator.setViewPager(viewpage_top_main)

        tabLayout!!.setSelectedTabIndicatorColor(resources.getColor(R.color.blue_main))
        tabLayout!!.setTabTextColors(resources.getColor(R.color.red), resources.getColor(R.color.white))

        viewPager_main!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    tabLayout!!.setSelectedTabIndicatorColor(resources.getColor(R.color.blue_main))
                    tabLayout!!.setTabTextColors(resources.getColor(R.color.red), resources.getColor(R.color.white))
                } else {
                    tabLayout!!.setSelectedTabIndicatorColor(resources.getColor(R.color.red))
                    tabLayout!!.setTabTextColors(resources.getColor(R.color.blue_main), resources.getColor(R.color.white))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        setCheckable(R.id.menu_drav_home)
    }


    private fun mescla() {
        if (tmdbMovies != null && tmdbTv != null) {
            var i = 0
            while (i < 20 && multi.size < 15) {
                if (i % 2 == 0) {
                    if (tmdbMovies!!.results.size > i) {
                        val topMain = TopMain()
                        val movieDb = tmdbMovies!!.results[i]
                        topMain.id = movieDb.id
                        topMain.nome = movieDb.title
                        topMain.mediaType = movieDb.mediaType.name
                        topMain.imagem = movieDb.backdropPath


                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        try {
                            val date = sdf.parse(movieDb.releaseDate)
                            if (movieDb.backdropPath != null && UtilsApp.verificaLancamento(date)) {
                                multi.add(topMain)
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show()
                        }

                    }

                } else {
                    if (tmdbTv!!.results.size > i) {
                        val topMain = TopMain()
                        val tv = tmdbTv!!.results[i]
                        topMain.id = tv.id
                        topMain.nome = tv.name
                        topMain.mediaType = tv.mediaType.name
                        topMain.imagem = tv.backdropPath
                        if (tv.backdropPath != null) {
                            multi.add(topMain)
                        }
                    }
                }
                i++
            }
        }
        setupViewPagerTabs()
    }

    private inner class TMDVAsync : AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg voids: Void?): Void? {
            var idioma_padrao = false
            if (!UtilsApp.isNetWorkAvailable(this@MainActivity2)) {
                return null
            }
            try {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@MainActivity2)
                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
            } catch (e: Exception) {
                FirebaseCrash.report(e)
            }


            if (idioma_padrao) {
                try {
                    tmdbTv = FilmeService.getTmdbTvShow()
                            .getAiringToday(BaseActivity.getLocale(), 1, UtilsApp.getTimezone())
                    tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies(BaseActivity.getLocale(), 1)
                } catch (e: Exception) {
                    //  Log.d(TAG, e.getMessage());
                    FirebaseCrash.report(e)
                    runOnUiThread { Toast.makeText(this@MainActivity2, R.string.ops, Toast.LENGTH_SHORT).show() }
                }


            } else {
                try {
                    tmdbTv = FilmeService.getTmdbTvShow().getAiringToday("en", 1, UtilsApp.getTimezone())
                    tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies("en", 1)
                } catch (e: Exception) {
                    // Log.d(TAG, e.toString());
                    FirebaseCrash.report(e)
                    runOnUiThread { Toast.makeText(this@MainActivity2, R.string.ops, Toast.LENGTH_SHORT).show() }
                }

            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            mescla()
        }
    }

    companion object {

        private val TAG = MainActivity::class.java.name
    }

}
