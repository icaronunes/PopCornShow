package listaserie.activity

/**
 * Created by icaro on 14/09/16.
 */

import activity.BaseActivity
import android.app.SearchManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import listaserie.fragment.TvShowsFragment
import utils.Constantes


class TvShowsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_main) // ???
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        setupNavDrawer()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val titulo = resources.getString(intent
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, 0))
        supportActionBar?.title = titulo

        val adview = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adview.loadAd(adRequest)

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
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.procurar)
        searchView.isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

