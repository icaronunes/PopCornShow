package produtora.activity

import activity.BaseActivity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.API
import domain.Company
import kotlinx.android.synthetic.main.produtora_layout.*
import produtora.adapter.ProdutoraAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.InfiniteScrollListener
import utils.UtilsApp


/**
 * Created by icaro on 10/08/16.
 */
class ProdutoraActivity : BaseActivity() {
    private var company: Company? = null
    private var pagina = 1
    private var id_produtora: Int = 0
    private var totalPagina = 1
    private var subscriptions = CompositeSubscription()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.produtora_layout)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsing_toolbar.title = " "
        id_produtora = intent.getIntExtra(Constantes.PRODUTORA_ID, 0)
        produtora_filmes_recycler.apply {
            val gridlayout = GridLayoutManager(this@ProdutoraActivity, 3)
            layoutManager = gridlayout
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(InfiniteScrollListener({getCompanyFilmes()}, gridlayout))
            adapter = ProdutoraAdapter()
        }
        getDadosCompany()
        getCompanyFilmes()

    }

    private fun getDadosCompany() {
            val inscricao = API(this).getCompany(id_produtora)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                       company = it
                        setImageTop()
                        collapsing_toolbar.title = company?.name
                    }, { erro ->
                        Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
                    })
            subscriptions.add(inscricao)
    }

    private fun getCompanyFilmes() {

        if (pagina <= totalPagina) {
            val inscricao = API(this).getCompanyFilmes(id_produtora, pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        pagina = it?.page!!
                        totalPagina = it?.totalPages!!
                        (produtora_filmes_recycler.adapter as ProdutoraAdapter).addprodutoraMovie(it.results
                                ?.sortedBy { it -> it?.releaseDate }
                                ?.reversed())
                        ++pagina
                    }, { e ->
                        Log.d(javaClass.simpleName, "Erro " + e.message)
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
        if (company?.logo_path != null) {
            Picasso.with(this)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(this, 4))!! + company?.logo_path)
                    .into(top_img_produtora)
            top_img_produtora.setColorFilter(resources.getColor(R.color.black_transparente_produtora), PorterDuff.Mode.DARKEN)
        } else {
            Picasso.with(this).load(R.drawable.empty_produtora2)
                    .into(top_img_produtora)
        }

        val animatorSet = AnimatorSet()
        val alphaStar = ObjectAnimator.ofFloat(top_img_produtora, "x", -100f, 0f)
                .setDuration(1700)
        animatorSet.playTogether(alphaStar)
        animatorSet.start()

    }

}

