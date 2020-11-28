package similares

import activity.BaseActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import domain.ResultsSimilarItem
import domain.tvshow.ResultsItem
import ifValid
import kotlinx.android.synthetic.main.activity_similares.adView
import kotlinx.android.synthetic.main.activity_similares.similares_recyclerview
import kotlinx.android.synthetic.main.activity_similares.text_similares_no_internet
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import tvshow.adapter.SimilaresListaSerieAdapter
import utils.Constant
import utils.UtilsApp
import utils.kotterknife.bindBundle

/**
 * Created by icaro on 12/08/16.
 */
open class SimilaresActivity(override var layout: Int = Layout.activity_similares) : BaseActivity() {

    private val listaFilme: List<ResultsSimilarItem?>? by bindBundle(Constant.SIMILARES_FILME)
    private val listaTvshow: List<ResultsItem?>? by bindBundle(Constant.SIMILARES_TVSHOW)
    private val title: String by bindBundle(Constant.NAME, "")

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setAdMob(adView)

        supportActionBar?.title = title

        similares_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@SimilaresActivity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }
        setDataRecycler()
    }

    private fun setDataRecycler() {
        if (UtilsApp.isNetWorkAvailable(this)) {
            listaFilme?.ifValid {
                similares_recyclerview.adapter = SimilaresListaFilmeAdapter(this@SimilaresActivity, listaFilme)
            }
            listaTvshow?.ifValid {
                similares_recyclerview.adapter = SimilaresListaSerieAdapter(this@SimilaresActivity, listaTvshow)
            }
            progress_horizontal.visibility = View.GONE
        } else {
            text_similares_no_internet.visibility = View.VISIBLE
            snack()
        }
    }

    private fun snack() {
        Snackbar.make(similares_recyclerview, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                setDataRecycler()
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
