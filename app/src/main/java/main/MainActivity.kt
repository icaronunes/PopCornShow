package main

import Color
import ID
import Layout
import Txt
import activity.BaseActivityAb
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.multidex.BuildConfig
import androidx.viewpager.widget.ViewPager
import applicaton.BaseViewModel.*
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import domain.ListaSeries
import domain.TopMain
import domain.movie.ListaFilmes
import fragment.ViewPageMainTopFragment
import kotlinx.android.synthetic.main.activity_main.activity_main_img
import kotlinx.android.synthetic.main.activity_main.indication_main
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager_main
import kotlinx.android.synthetic.main.activity_main.viewpage_top_main
import utils.UtilsApp
import utils.gone
import utils.visible

class MainActivity(override var layout: Int = Layout.activity_main) : BaseActivityAb() {
	private val model: MainViewModel by lazy { createViewModel(MainViewModel::class.java, this) }
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(model)
		setUpToolBar()
		setupNavDrawer()
		setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		animation()
		setupViewBotton()
		setObservers()
	}

	private fun setObservers() {
		model.topo.observe(this, Observer {
			when (it) {
				is BaseRequest.Success -> mescla(it.result.first, it.result.second)
				is BaseRequest.Failure -> snack(viewpage_top_main, getString(Txt.erro_seguir)) {
					model.fetchAll()
				}
			}
		})

		model.new.observe(this, Observer {
			if (it) news()
		})

		model.animed.observe(this, Observer {
			logonAnimated(it)
		})
	}

	private fun logonAnimated(it: Boolean) {
		if (it) {
			activity_main_img?.visible()
		} else {
			activity_main_img?.gone()
		}
	}

	private fun news() {
		AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_popcorn2)
			.setTitle(R.string.novidades_title)
			.setMessage(R.string.novidades_text)
			.setPositiveButton(R.string.ok) { _, _ ->
				val sharedPref = PreferenceManager.getDefaultSharedPreferences(application)
				val editor = sharedPref.edit()
				editor.putBoolean(BuildConfig.VERSION_CODE.toString(), false)
				editor.remove((BuildConfig.VERSION_CODE - 1).toString())
				editor.apply()
			}.create().show()
	}

	private fun animation() {
		AnimatorSet().apply {
			play(ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 0.1f, 1f)).apply {
				with(ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 1f, 0.1f))
				with(ObjectAnimator.ofFloat(activity_main_img, View.ALPHA, 0.1f, 1f))
			}
			duration = 500
			interpolator = AccelerateDecelerateInterpolator()
			start()
		}
	}

	private fun snack() {
		Snackbar.make(viewpage_top_main, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					model.fetchAll()
				} else {
					snack()
				}
			}.show()
	}

	private fun setupViewPagerTabs(multi: List<TopMain>) {
		viewpage_top_main.offscreenPageLimit = 2
		viewpage_top_main.adapter = ViewPageMainTopFragment(supportFragmentManager, multi)
		indication_main.setViewPager(viewpage_top_main)
		model.animation(false)
	}

	private fun setupViewBotton() {
		viewPager_main.apply {
			offscreenPageLimit = 2
			currentItem = 0
			adapter = MainAdapter(this@MainActivity, supportFragmentManager)
		}
		tabLayout.apply {
			setupWithViewPager(viewPager_main)
			setSelectedTabIndicatorColor(
				ContextCompat.getColor(
					this@MainActivity,
					Color.blue_main
				)
			)
			setTabTextColors(
				ContextCompat.getColor(this@MainActivity, Color.red),
				ContextCompat.getColor(this@MainActivity, Color.white)
			)
		}

		viewPager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageSelected(position: Int) {
				if (position == 0) {
					tabLayout.setSelectedTabIndicatorColor(
						ContextCompat.getColor(
							baseContext,
							Color.blue_main
						)
					)
					tabLayout.setTabTextColors(
						ContextCompat.getColor(baseContext, Color.red),
						ContextCompat.getColor(baseContext, Color.white)
					)
				} else {
					tabLayout.setSelectedTabIndicatorColor(
						ContextCompat.getColor(
							baseContext,
							Color.red
						)
					)
					tabLayout.setTabTextColors(
						ContextCompat.getColor(
							baseContext,
							Color.blue_main
						), ContextCompat.getColor(baseContext, Color.white)
					)
				}
			}

			override fun onPageScrolled(
				position: Int,
				positionOffset: Float,
				positionOffsetPixels: Int
			) {
			}

			override fun onPageScrollStateChanged(state: Int) {}
		})
	}

	override fun onResume() {
		super.onResume()
		setCheckable(ID.menu_drav_home)
	}

	private fun mescla(tmdbMovies: ListaFilmes, tmdbTv: ListaSeries) {
		val listMovie = tmdbMovies.results.filter {
			!it.backdropPath.isNullOrBlank() && !it.releaseDate.isNullOrBlank()
		}
		val listTv = tmdbTv.results.filter {
			!it.backdropPath.isNullOrBlank()
		}
		val newList = mutableListOf<TopMain>().apply {
			listMovie.zip(listTv).forEach { pair ->
				val topMain = TopMain()
				pair.first.apply {
					topMain.id = id
					topMain.nome = title
					topMain.mediaType = "movie"
					topMain.imagem = backdropPath
				}
				add(topMain)
				val topMainTv = TopMain()
				pair.second.apply {
					topMainTv.id = id
					topMainTv.nome = name
					topMainTv.mediaType = "tv"
					topMainTv.imagem = backdropPath
				}
				add(topMainTv)
			}
		}
		setupViewPagerTabs(newList)
	}
}
