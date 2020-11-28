package lista.movie.activity

import Layout
import activity.BaseActivity
import android.os.Bundle
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lista.movie.fragment.MoviesFragment
import utils.Api
import utils.Constant

@ExperimentalCoroutinesApi
class MoviesActivity(override var layout: Int = Layout.activity_list_main) : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(layout)
		setUpToolBar()
		setupNavDrawer()
		getTitleChoose()
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		if (savedInstanceState == null) {
			supportFragmentManager
				.beginTransaction()
				.add(id.container_list_main, MoviesFragment().apply {
                    arguments = intent.extras
                })
				.commit()
		}
	}

	private fun getTitleChoose() {
		supportActionBar?.title = when (intent.getStringExtra(Constant.NAV_DRAW_ESCOLIDO)) {
            Api.TYPESEARCH.MOVIE.now -> getString(R.string.now_playing)
            Api.TYPESEARCH.MOVIE.upComing -> getString(R.string.upcoming)
            Api.TYPESEARCH.MOVIE.popular -> getString(R.string.populares)
            Api.TYPESEARCH.MOVIE.bestScore -> getString(R.string.top_rated)
			else -> getString(R.string.app_name)
		}
	}
}