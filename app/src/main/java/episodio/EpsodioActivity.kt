package episodio

import Layout
import activity.BaseActivityAb
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import domain.TvSeasons
import kotlinx.android.synthetic.main.activity_epsodios.adView
import kotlinx.android.synthetic.main.activity_epsodios.tabLayout_epsodio
import kotlinx.android.synthetic.main.activity_epsodios.viewpager_epsodio
import utils.Constant
import utils.kotterknife.bindBundle

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioActivity(override var layout: Int = Layout.activity_epsodios) : BaseActivityAb() {

	private val tvshowId: Int by bindBundle(Constant.TVSHOW_ID)
	private val posicao: Int by bindBundle(Constant.POSICAO)
	private val color: Int by bindBundle(Constant.COLOR_TOP)
	private val seasonPosition: Int by bindBundle(Constant.TEMPORADA_POSITION)
	private val fallow: Boolean by bindBundle(Constant.SEGUINDO, false)
	private val tvSeason: TvSeasons by bindBundle(Constant.TVSEASONS)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		hangleTitle()
		setViewPager()
		setAdMob(adView)
	}

	private fun hangleTitle() {
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = tvSeason.name ?: intent.getStringExtra(Constant.NAME)
	}

	private fun setViewPager() {
		viewpager_epsodio.run {
			offscreenPageLimit = 4
			adapter = EpsodioAdapter(
				supportFragmentManager, tvSeason,
				tvshowId, color, fallow, seasonPosition
			)
			currentItem = posicao
			tabLayout_epsodio.setupWithViewPager(this)
			tabLayout_epsodio.setSelectedTabIndicatorColor(color)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu) = true

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
		}
		return super.onOptionsItemSelected(item)
	}
}
