package activity

import adapter.SimilaresAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import br.com.icaro.filme.R
import domain.ResultsSimilarItem
import kotlinx.android.synthetic.main.activity_similares.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import utils.Constantes
import utils.UtilsApp


/**
 * Created by icaro on 12/08/16.
 */
class SimilaresActivity : BaseActivity() {

    private var text_similares_no_internet: TextView? = null
    private var lista: List<ResultsSimilarItem?>? = null
    private var title: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_similares)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()

        supportActionBar?.title = title

        similares_recyckeview.apply {
            layoutManager = LinearLayoutManager(this@SimilaresActivity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            similares_recyckeview.adapter = SimilaresAdapter(this@SimilaresActivity, lista)
            progress_horizontal.visibility = View.GONE
        } else {
            text_similares_no_internet!!.visibility = View.VISIBLE
            snack()
        }
    }

    private fun getExtras() {

        lista = intent.getSerializableExtra(Constantes.SIMILARES) as List<ResultsSimilarItem?>?
        title = intent.getStringExtra(Constantes.NOME_FILME)

    }

    protected fun snack() {
        Snackbar.make(similares_recyckeview, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        text_similares_no_internet?.visibility = View.GONE
                    } else {
                        snack()
                    }
                }.show()
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

}
