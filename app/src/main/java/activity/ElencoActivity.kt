package activity

import adapter.ElencoAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.icaro.filme.R
import domain.CastItem
import kotlinx.android.synthetic.main.activity_elenco.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 24/07/16.
 */
class ElencoActivity : BaseActivity() {

    private var season = -100
    private var title: String? = null
    private var lista: List<CastItem?>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elenco)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getExtras()

        supportActionBar!!.title = title

        elenco_recycleview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, lista)
            progress_horizontal.visibility = View.GONE

        } else {
            text_elenco_no_internet?.visibility = View.VISIBLE
            snack()
        }

    }

    private fun getExtras() {

        title = intent.getStringExtra(Constantes.NOME)
        lista = intent.getSerializableExtra(Constantes.ELENCO) as List<CastItem?>?
        season = intent.getIntExtra(Constantes.TVSEASONS, -100)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun snack() {
        Snackbar.make(linear_elenco_layout!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        text_elenco_no_internet?.visibility = View.GONE
                        elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, lista)
                    } else {
                        snack()
                    }
                }.show()
    }
}