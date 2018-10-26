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
import android.widget.Toast
import br.com.icaro.filme.R
import domain.Api
import domain.CastItem
import kotlinx.android.synthetic.main.activity_elenco.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 24/07/16.
 */
class ElencoActivity : BaseActivity() {

    private var season = -100
    private var title: String? = null
    private var lista: List<CastItem?>? = null
    private var id: Int = 0
    private var subscriptions = CompositeSubscription()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elenco)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()

        supportActionBar?.title = title

        elenco_recycleview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            if (id == 0 && season == -100) {
                elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, lista)
                progress_horizontal.visibility = View.GONE
            } else {
                val inscricaoMovie = Api(context = this).getTvCreditosTemporada(id, season)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, it.cast)
                            progress_horizontal.visibility = View.GONE
                        }, { erro ->
                            Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        })

                subscriptions.add(inscricaoMovie)
            }

        } else {
            text_elenco_no_internet?.visibility = View.VISIBLE
            snack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun getExtras() {

        title = intent.getStringExtra(Constantes.NOME)
        lista = intent.getSerializableExtra(Constantes.ELENCO) as List<CastItem?>?
        season = intent.getIntExtra(Constantes.TVSEASONS, -100)
        id = intent.getIntExtra(Constantes.ID, 0)

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
                        if (id == 0 && season == -100) {
                            elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, lista)
                            progress_horizontal.visibility = View.GONE
                        } else {
                            val inscricaoMovie = Api(context = this).getTvCreditosTemporada(id, season)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        elenco_recycleview.adapter = ElencoAdapter(this@ElencoActivity, it.cast)
                                        progress_horizontal.visibility = View.GONE
                                    }, { erro ->
                                        Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                                    })

                            subscriptions.add(inscricaoMovie)
                        }
                    } else {
                        text_elenco_no_internet?.visibility = View.VISIBLE
                        snack()
                    }
                }.show()
    }
}