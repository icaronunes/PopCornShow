package episodio

import activity.BaseActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import br.com.icaro.filme.R
import domain.TvSeasons
import kotlinx.android.synthetic.main.activity_epsodios.*
import utils.Constantes

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioActivity : BaseActivity() {

    private var tvshowId: Int = 0
    private var posicao: Int = 0
    private var color: Int = 0
    private var temporadaPosition: Int = 0
    private lateinit var tvSeason: TvSeasons
    private var fallow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epsodios)
        setUpToolBar()
        setExtras()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val fragmentManager = supportFragmentManager
        setViewPager(fragmentManager)
        setAdMob(adView)
    }

    private fun setViewPager(fragmentManager: FragmentManager) {
        viewpager_epsodio.run {
            offscreenPageLimit = 4
            adapter = EpsodioAdapter(fragmentManager, tvSeason,
                    tvshowId, color, fallow, temporadaPosition)
            viewpager_epsodio.currentItem = posicao
            tabLayout_epsodio.setupWithViewPager(this)
            tabLayout_epsodio.setSelectedTabIndicatorColor(color)
        }
    }

    private fun setExtras() {
        tvshowId = intent.getIntExtra(Constantes.TVSHOW_ID, 0)
        posicao = intent.getIntExtra(Constantes.POSICAO, 0)
        color = intent.getIntExtra(Constantes.COLOR_TOP, 0)
        temporadaPosition = intent.getIntExtra(Constantes.TEMPORADA_POSITION, 0)
        fallow = intent.getBooleanExtra(Constantes.SEGUINDO, false)
        (intent.getSerializableExtra(Constantes.TVSEASONS) as TvSeasons).let {
            tvSeason = it
        }
        supportActionBar?.title = if (tvSeason.name!!.isNotEmpty()) tvSeason.name else intent.getStringExtra(Constantes.NOME)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
