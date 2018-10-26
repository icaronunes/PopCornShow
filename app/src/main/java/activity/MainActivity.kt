package activity

import adapter.MainAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import br.com.icaro.filme.BuildConfig
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.viewpagerindicator.CirclePageIndicator
import domain.API
import domain.FilmeService
import domain.ListaSeries
import domain.movie.ListaFilmes
import domain.TopMain
import fragment.ViewPageMainTopFragment
import info.movito.themoviedbapi.TvResultsPage
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import utils.UtilsApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {

    private var viewPager_main: ViewPager? = null
    private var viewpage_top_main: ViewPager? = null
    private var img: ImageView? = null
    private var tabLayout: TabLayout? = null
    private val multi = mutableListOf<TopMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        setupNavDrawer()
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(" ")


        img = findViewById(R.id.activity_main_img)

        if (UtilsApp.isNetWorkAvailable(this)) {
            getTopoLista(UI)
            setupViewBotton()
        } else {
            snack()
        }

        animacao()
        novidades()

    }

    private fun novidades() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)) {
            val dialog = AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_popcorn2)
                    .setTitle(R.string.novidades_title)
                    .setMessage(R.string.novidades_text)
                    .setPositiveButton(R.string.ok) { dialogInterface, i ->
                        val editor = sharedPref.edit()
                        editor.putBoolean(BuildConfig.VERSION_CODE.toString(), false)
                        editor.remove((BuildConfig.VERSION_CODE - 1).toString())
                        editor.apply()
                    }.create()
            dialog.show()
        }
    }

    private fun getTopoLista(UI: HandlerContext) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val movies = async(Dispatchers.Default) { API(this@MainActivity).getNowPlayingMovies(this@MainActivity) }
                val tvshow = async(Dispatchers.Default) { API(this@MainActivity).getAiringToday(this@MainActivity) }

                mescla(tmdbMovies = movies.await(), tmdbTv = tvshow.await())
            } catch (ex: Exception) {
                Toast.makeText(this@MainActivity, getString(R.string.ops), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun animacao() {
        val set = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(img, View.ALPHA, 0.1f, 1f)
        animator.duration = 5000
        val animator2 = ObjectAnimator.ofFloat(img, View.ALPHA, 1f, 0.1f)
        animator.duration = 5000
        val animator3 = ObjectAnimator.ofFloat(img, View.ALPHA, 0.1f, 1f)
        animator.duration = 5000
        set.playSequentially(animator, animator2, animator3)
        set.start()


    }

    private fun snack() {
        Snackbar.make(viewpage_top_main!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        getTopoLista(UI)
                    } else {
                        snack()
                    }
                }.show()
    }

    private fun setupViewPagerTabs() {

        viewpage_top_main = findViewById<View>(R.id.viewpage_top_main) as ViewPager
        viewpage_top_main!!.offscreenPageLimit = 3
        viewpage_top_main!!.adapter = ViewPageMainTopFragment(supportFragmentManager, multi)
        val circlePageIndicator = findViewById<View>(R.id.indication_main) as CirclePageIndicator
        circlePageIndicator.setViewPager(viewpage_top_main)
    }

    private fun setupViewBotton() {

        viewPager_main = findViewById<View>(R.id.viewPager_main) as ViewPager
        viewPager_main!!.offscreenPageLimit = 2
        tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        viewPager_main!!.currentItem = 0
        viewPager_main!!.adapter = MainAdapter(this, supportFragmentManager)
        tabLayout!!.setupWithViewPager(viewPager_main)

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

    private fun mescla(tmdbMovies: ListaFilmes, tmdbTv: ListaSeries) {

        val listaFilmes = tmdbMovies.results.filter {
            !it?.backdropPath.isNullOrBlank() && !it?.releaseDate.isNullOrBlank()
        }.toList()

        val listaTv = tmdbTv.results.filter {
            !it.backdropPath.isNullOrBlank()
        }.toList()

        for (index in 0..15) {
            val topMain = TopMain()
            if (index % 2 == 0) {
                if (index <= listaFilmes.size) {
                    val movieDb = listaFilmes[index]!!
                    topMain.id = movieDb.id
                    topMain.nome = movieDb.title
                    topMain.mediaType = "movie"
                    topMain.imagem = movieDb.backdropPath
                    multi.add(topMain)
                }
            } else {
                if (index <= listaTv.size) {
                    val tv = listaTv[index]!!
                    topMain.id = tv.id!!
                    topMain.nome = tv.name
                    topMain.mediaType = "tv"
                    topMain.imagem = tv.backdropPath
                    multi.add(topMain)
                }
            }
        }

        setupViewPagerTabs()
        img!!.visibility = View.GONE
    }

}
