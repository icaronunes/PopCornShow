package activity

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
import domain.tvshow.ResultsItem
import filme.adapter.SimilaresListaFilmeAdapter
import kotlinx.android.synthetic.main.activity_similares.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import tvshow.adapter.SimilaresListaSerieAdapter
import utils.Constantes
import utils.UtilsApp


/**
 * Created by icaro on 12/08/16.
 */
class SimilaresActivity : BaseActivity() {

    private var text_similares_no_internet: TextView? = null
    private var listaFilme: List<ResultsSimilarItem?>? = null
    private var listaTvshow: List<ResultsItem?>? = null
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

        if (UtilsApp.isNetWorkAvailable(this)) {
            if (listaFilme != null) {
                similares_recyckeview.adapter = SimilaresListaFilmeAdapter(this@SimilaresActivity, listaFilme)
            } else if (listaTvshow != null) {
                similares_recyckeview.adapter = SimilaresListaSerieAdapter(this@SimilaresActivity, listaTvshow!!)
            }
            progress_horizontal.visibility = View.GONE
        } else {
            text_similares_no_internet?.visibility = View.VISIBLE
            snack()
        }
    }

    private fun getExtras() {
        if (intent.hasExtra(Constantes.SIMILARES_FILME)) {
            listaFilme = intent.getSerializableExtra(Constantes.SIMILARES_FILME) as List<ResultsSimilarItem?>?
        } else if (intent.hasExtra(Constantes.SIMILARES_TVSHOW)) {
            listaTvshow = intent.getSerializableExtra(Constantes.SIMILARES_TVSHOW) as List<ResultsItem?>?
        }
        title = intent.getStringExtra(Constantes.NOME)

    }

    protected fun snack() {
        Snackbar.make(similares_recyckeview, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(this)) {

                        if (listaFilme?.isEmpty()!!) {
                            similares_recyckeview.adapter = SimilaresListaFilmeAdapter(this@SimilaresActivity, listaFilme)
                        } else if (listaTvshow?.isEmpty()!!) {
                            similares_recyckeview.adapter = SimilaresListaSerieAdapter(this@SimilaresActivity, listaTvshow)
                        }
                        progress_horizontal.visibility = View.GONE
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
