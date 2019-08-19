package listaserie.activity

/**
 * Created by icaro on 14/09/16.
 */

import activity.BaseActivity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import br.com.icaro.filme.R
import listaserie.fragment.TvShowsFragment
import utils.Constantes


class TvShowsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_main) // ???
        setUpToolBar()
        setupNavDrawer()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val titulo = resources.getString(intent
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, 0))
        supportActionBar?.title = titulo
        
        if (savedInstanceState == null) {
            val tvShowsFragment = TvShowsFragment()
            tvShowsFragment.arguments = intent.extras
            setCheckable(intent.getIntExtra(Constantes.ABA, 0))
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
}

