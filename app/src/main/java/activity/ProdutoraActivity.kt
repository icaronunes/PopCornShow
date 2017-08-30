package activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.API
import domain.Company
import info.movito.themoviedbapi.TmdbCompany
import kotlinx.android.synthetic.main.produtora_layout.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsFilme

/**
 * Created by icaro on 10/08/16.
 */
class ProdutoraActivity : BaseActivity() {
    private var company: Company? = null
    private var pagina = 1
    private var resultsPage: TmdbCompany.CollectionResultsPage? = null
    private var id_produtora: Int = 0
    private val TAG = this.javaClass.name
    private var totalPagina = 1
    private var subscriptions = CompositeSubscription()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.produtora_layout)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        id_produtora = intent.getIntExtra(Constantes.PRODUTORA_ID, 0)
        produtora_filmes_recycler.apply {
            layoutManager = GridLayoutManager(this@ProdutoraActivity, 3)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }
        InfoLayout()
        getDados()

        /*        AdView adview = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                        .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                        .build();
                adview.loadAd(adRequest);*/

        if (!coordinator_layout.hasFocus()) {
            info_layout.visibility = View.INVISIBLE
        }

    }

    fun getDados() {
        if (totalPagina >= pagina) {
            val inscricao = API().getCompany(id_produtora)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                       company = it
                        setHeadquarters()
                        setHome()
                        setImageTop()
                    }, { erro ->
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
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

    private fun setHeadquarters() {
        if (company!!.headquarters != null) {
            headquarters!!.text = company!!.headquarters
            headquarters!!.visibility = View.VISIBLE
        } else {
            headquarters!!.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setHome() {
        if (company!!.homepage != null) {
            home_produtora!!.text = company!!.homepage
            home_produtora!!.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(company!!.homepage)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } else {
            home_produtora!!.visibility = View.GONE
        }

    }

    private fun setImageTop() {
        if (company?.logoPath != null) {
            Picasso.with(this)
                    .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(this, 4))!! + company?.logoPath)
                    .into(top_img_produtora)
        } else {
            Picasso.with(this).load(R.drawable.empty_produtora2)
                    .into(top_img_produtora)
        }

        val animatorSet = AnimatorSet()
        val alphaStar = ObjectAnimator.ofFloat(top_img_produtora, "x", -100f, 0f)
                .setDuration(1000)
        animatorSet.playTogether(alphaStar)
        animatorSet.start()

        top_img_produtora?.setOnClickListener {
            if (info_layout!!.visibility == View.INVISIBLE) {
                info_layout!!.visibility = View.VISIBLE
            } else {
                if (info_layout!!.visibility == View.VISIBLE) {
                    info_layout!!.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun InfoLayout() {

        info_layout!!.setOnClickListener {
            if (info_layout.visibility == View.INVISIBLE) {
                info_layout.visibility = View.VISIBLE
            } else {
                if (info_layout!!.visibility == View.VISIBLE) {
                    info_layout!!.visibility = View.INVISIBLE
                }
            }
        }
    }


//    private inner class TMDVAsync : AsyncTask<Void, Void, MovieDb>() {
//
//        override fun doInBackground(vararg voids: Void): MovieDb? {
//            //não é possivel buscar TVShow da company. Esperar API
//            var idioma_padrao = false
//            try {
//                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@ProdutoraActivity)
//                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
//            } catch (e: Exception) {
//                FirebaseCrash.report(e)
//            }
//
//            try {
//                company = getTmdbCompany().getCompanyInfo(id_produtora)
//                if (pagina == 1) {
//                    if (idioma_padrao) {
//                        resultsPage = FilmeService.getTmdbCompany()
//                                .getCompanyMovies(id_produtora, Locale.getDefault().language + "-" + Locale.getDefault().country + ",en,null", pagina)
//                    } else {
//                        resultsPage = FilmeService.getTmdbCompany()
//                                .getCompanyMovies(id_produtora, "en,null", pagina)
//                    }
//                } else {
//                    if (idioma_padrao) {
//                        val temp = resultsPage
//                        resultsPage = FilmeService.getTmdbCompany()
//                                .getCompanyMovies(id_produtora, Locale.getDefault().language + "-" + Locale.getDefault().country + ",en,null", pagina)
//                        resultsPage!!.results.addAll(temp!!.results)
//                    } else {
//                        resultsPage = FilmeService.getTmdbCompany()
//                                .getCompanyMovies(id_produtora, "en,null", pagina)
//                    }
//                }
//            } catch (e: Exception) {
//                FirebaseCrash.report(e)
//                //  Log.d(TAG, e.getMessage());
//                runOnUiThread { Toast.makeText(this@ProdutoraActivity, R.string.ops, Toast.LENGTH_SHORT).show() }
//            }
//
//            return null
//        }
//
//        override fun onPostExecute(movieDb: MovieDb) {
//            super.onPostExecute(movieDb)
//            //  Log.d("PRODUTORA", "post : " + resultsPage.getTotalPages());
//            refreshLayout!!.isRefreshing = false
//            if (pagina == 1) {
//                setImageTop()
//            }
//            if (pagina <= resultsPage!!.totalPages) {
//                pagina = pagina + 1
//            }
//            progressBar!!.visibility = View.GONE
//            setHome()
//            setHeadquarters()
//            if (company != null) {
//                supportActionBar!!.title = if (company!!.name.isEmpty()) "" else company!!.name
//            }
//            recyclerView!!.adapter = ProdutoraAdapter(this@ProdutoraActivity,
//                    if (resultsPage != null) resultsPage!!.results else null)
//        }
//    }
}
