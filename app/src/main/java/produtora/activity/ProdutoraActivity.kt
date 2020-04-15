package produtora.activity

import activity.BaseActivity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.produtora_layout.collapsing_toolbar
import kotlinx.android.synthetic.main.produtora_layout.produtora_filmes_recycler
import kotlinx.android.synthetic.main.produtora_layout.toolbar
import kotlinx.android.synthetic.main.produtora_layout.top_img_produtora
import produtora.adapter.ProdutoraAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Api
import utils.Constant
import utils.InfiniteScrollListener
import utils.setPicasso

/**
 * Created by icaro on 10/08/16.
 */
class ProdutoraActivity : BaseActivity() {
    private var logo: String? = ""
    private var name: String? = ""
    private var pagina = 1
    private var idProdutora: Int = 0
    private var totalPagina = 1
    private var subscriptions = CompositeSubscription()

    // TODO adicionar propaganda na lista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.produtora_layout)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setExtras()
        setRecycler()
        setImageTop()
        getCompanyFilmes()
    }

    private fun setRecycler() {
        produtora_filmes_recycler.apply {
            val gridlayout = GridLayoutManager(this@ProdutoraActivity, 3)
            layoutManager = gridlayout
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(InfiniteScrollListener({ getCompanyFilmes() }, gridlayout))
            adapter = ProdutoraAdapter()
        }
    }

    private fun setExtras() {
        idProdutora = intent.getIntExtra(Constant.PRODUTORA_ID, 0)
        name = intent.getStringExtra(Constant.NAME)
        logo = intent.getStringExtra(Constant.ENDERECO)
    }

    private fun setTitle() {
        toolbar?.title = name
        collapsing_toolbar?.title = name
        top_img_produtora.scaleType = ImageView.ScaleType.CENTER_CROP
        toolbar.titleMarginTop = 15
        toolbar.titleMarginStart = 25
    }

    private fun getCompanyFilmes() {
        if (pagina <= totalPagina) {
            val inscricao = Api(this).getCompanyFilmes(idProdutora, pagina)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ companyFilmes ->
                    pagina = companyFilmes?.page!!
                    totalPagina = companyFilmes.totalPages!!

                    (produtora_filmes_recycler.adapter as ProdutoraAdapter).addProdutoraMovie(companyFilmes.results
                        ?.sortedBy { it?.releaseDate }
                        ?.reversed(), companyFilmes.totalResults!!)
                    ++pagina
                }, {
                    Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                })
            subscriptions.add(inscricao)
        }
    }

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setImageTop() {
        val setupTop = {
            AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(top_img_produtora, "x", -100f, 0f)
                    .setDuration(400))
            }.start()
            toolbar?.title = " "
            collapsing_toolbar?.title = " "
        }

        logo.let {
            top_img_produtora.setPicasso(it, 4, img_erro = R.drawable.empty_produtora2, sucesso = setupTop, error = { setTitle() })
        }
    }
}
