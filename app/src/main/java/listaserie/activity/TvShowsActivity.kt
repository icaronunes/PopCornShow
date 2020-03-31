package listaserie.activity

/**
 * Created by icaro on 14/09/16.
 */

import activity.BaseActivity
import android.os.Bundle
import android.view.Menu
import br.com.icaro.filme.R
import listaserie.fragment.TvShowsFragment
import utils.Api
import utils.Constant

class TvShowsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_main)
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
        supportActionBar!!.title = when (intent.getStringExtra(Constant.NAV_DRAW_ESCOLIDO)) {
            Api.TYPESEARCH.SERIE.hoje -> getString(R.string.today)
            Api.TYPESEARCH.SERIE.semana -> getString(R.string.air_date_main)
            Api.TYPESEARCH.SERIE.popular -> getString(R.string.populares)
            Api.TYPESEARCH.SERIE.melhores -> getString(R.string.top_rated)
            else -> getString(R.string.app_name)
        }
    }
}
