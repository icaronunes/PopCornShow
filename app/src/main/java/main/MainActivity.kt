package main

import activity.BaseActivity
import adapter.MainAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.BuildConfig
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import com.viewpagerindicator.CirclePageIndicator
import domain.ListaSeries
import domain.TopMain
import domain.movie.ListaFilmes
import fragment.ViewPageMainTopFragment
import kotlinx.android.synthetic.main.activity_main.*
import utils.UtilsApp


class MainActivity : BaseActivity() {

    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = createViewModel(MainViewModel::class.java)
        setUpToolBar()
        setupNavDrawer()
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        animacao()
        if (UtilsApp.isNetWorkAvailable(this)) {
            model.getTopoLista()
        } else {
            snack()
        }
        setupViewBotton()
        setObservers()
        model.novidade()
    }

    private fun setObservers() {
        model.data.observe(this, Observer {
            when(it) {
                is MainViewModel.MainModel.Data ->  mescla(it.data.first, it.data.second)
                is MainViewModel.MainModel.isNovidade -> novidades()
                is MainViewModel.MainModel.VisibleAnimed -> visibleAnimed(it)
            }
        })
    }

    private fun visibleAnimed(it: MainViewModel.MainModel.VisibleAnimed) {
        if (it.visible) {
            activity_main_img?.visibility = View.VISIBLE
        } else {
            activity_main_img?.visibility = View.GONE
        }
    }

    private fun novidades() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(application)
            val dialog = AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_popcorn2)
                    .setTitle(R.string.novidades_title)
                    .setMessage(R.string.novidades_text)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        val editor = sharedPref.edit()
                        editor.putBoolean(BuildConfig.VERSION_CODE.toString(), false)
                        editor.remove((BuildConfig.VERSION_CODE - 1).toString())
                        editor.apply()
                    }.create()
            dialog.show()
    }

    private fun animacao() {
        //TODO atualizar animação
        val set = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 0.1f, 1f)
        animator.duration = 5000
        val animator2 = ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 1f, 0.1f)
        animator.duration = 5000
        val animator3 = ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 0.1f, 1f)
        animator.duration = 5000
        set.playSequentially(animator, animator2, animator3)
        set.start()
    }

    private fun snack() {
        Snackbar.make(viewpage_top_main, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        model.getTopoLista()
                        setupViewBotton()
                    } else {
                        snack()
                    }
                }.show()
    }

    private fun setupViewPagerTabs(multi: MutableList<TopMain>) {
        viewpage_top_main.offscreenPageLimit = 2
        viewpage_top_main.adapter = ViewPageMainTopFragment(supportFragmentManager, multi)
        findViewById<CirclePageIndicator>(R.id.indication_main)
                .setViewPager(viewpage_top_main)
        model.animation(false)
    }

    private fun setupViewBotton() {

        viewPager_main.offscreenPageLimit = 2
        viewPager_main.currentItem = 0
        viewPager_main.adapter = MainAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager_main)

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.blue_main))
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.white))

        viewPager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(baseContext, R.color.blue_main))
                    tabLayout.setTabTextColors(ContextCompat.getColor(baseContext, R.color.red), ContextCompat.getColor(baseContext, R.color.white))
                } else {
                    tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(baseContext, R.color.red))
                    tabLayout.setTabTextColors(ContextCompat.getColor(baseContext, R.color.blue_main), ContextCompat.getColor(baseContext, R.color.white))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
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

        val multi = mutableListOf<TopMain>().apply {
            for (index in 0..15) {
                val topMain = TopMain()// criar class utilitaria
                if (index % 2 == 0) {
                    if (index <= listaFilmes.size) {
                        val movieDb = listaFilmes[index]!!
                        topMain.id = movieDb.id
                        topMain.nome = movieDb.title
                        topMain.mediaType = "movie"
                        topMain.imagem = movieDb.backdropPath
                        add(topMain)
                    }
                } else {
                    if (index <= listaTv.size) {
                        val tv = listaTv[index]
                        topMain.id = tv.id!!
                        topMain.nome = tv.name
                        topMain.mediaType = "tv"
                        topMain.imagem = tv.backdropPath
                        add(topMain)
                    }
                }
            }
        }
        setupViewPagerTabs(multi)
    }
}
