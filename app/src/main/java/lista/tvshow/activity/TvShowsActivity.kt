package lista.tvshow.activity

/**
 * Created by icaro on 14/09/16.
 */
import Layout
import activity.BaseActivityAb
import android.os.Bundle
import android.view.Menu
import br.com.icaro.filme.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lista.tvshow.fragment.TvShowsFragment
import utils.Api
import utils.Constant

@ExperimentalCoroutinesApi
class TvShowsActivity(override var layout: Int = Layout.activity_list_main) : BaseActivityAb() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(layout)
		setUpToolBar()
		setupNavDrawer()
		getTitleChoose()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		if (savedInstanceState == null) {
			val tvShowsFragment = TvShowsFragment()
			tvShowsFragment.arguments = intent.extras
			supportFragmentManager
                .beginTransaction()
				.add(R.id.container_list_main, tvShowsFragment)
				.commit()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu, menu)
		return true
	}

	private fun getTitleChoose() {
		supportActionBar?.title = when (intent.getStringExtra(Constant.NAV_DRAW_ESCOLIDO)) {
            Api.TYPESEARCH.TVSHOW.toDay -> getString(R.string.today)
            Api.TYPESEARCH.TVSHOW.week -> getString(R.string.air_date_main)
            Api.TYPESEARCH.TVSHOW.popular -> getString(R.string.populares)
            Api.TYPESEARCH.TVSHOW.bestScore -> getString(R.string.top_rated)
			else -> getString(R.string.app_name)
		}
	}
}
