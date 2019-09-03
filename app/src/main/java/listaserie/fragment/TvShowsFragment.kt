package listaserie.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.icaro.filme.R
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.snackbar.Snackbar
import domain.Api
import fragment.FragmentBase
import kotlinx.android.synthetic.main.fragment_list_medias.*
import listaserie.adapter.ListaSeriesAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.Constantes
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.UtilsKt
import utils.UtilsKt.Companion.getIdiomaEscolhido
import java.util.concurrent.TimeUnit

/**
 * Created by icaro on 14/09/16.
 */
class TvShowsFragment : FragmentBase() {

    private var abaEscolhida: Int? = null
    private var pagina = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            this.abaEscolhida = arguments?.getInt(Constantes.NAV_DRAW_ESCOLIDO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_list_medias, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAdMob(adView)
        recycle_listas.apply {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this.layoutManager = layoutManager
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(InfiniteScrollStaggeredListener({}, { getListaSereies() }, layoutManager))
            setHasFixedSize(true)
            adapter = ListaSeriesAdapter(context)
        }

        if (!UtilsApp.isNetWorkAvailable(context)) {
            txt_listas.visibility = View.VISIBLE
            txt_listas.text = resources.getString(R.string.no_internet)
            snack()

        } else {
            getListaSereies()
        }
    }

    private fun snack() {
        Snackbar.make(frame_list_filme, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(context)) {
                        txt_listas.visibility = View.INVISIBLE
                        getListaSereies()
                    } else {
                        snack()
                    }
                }.show()
    }

    fun getListaSereies() {

        val inscricao = Api(context!!)
                .buscaDeSeries(getListaTipo(), pagina = pagina, local = getIdiomaEscolhido(context!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(2, TimeUnit.SECONDS)
                .subscribe({
                    if (view != null) {
                        if (pagina == it.page) {
                            (recycle_listas.adapter as ListaSeriesAdapter).addSeries(it.results)
                            pagina = it.page
                            ++pagina

                            UtilsKt.getAnuncio(context!!, 2) { nativeAd: UnifiedNativeAd ->
                                if (recycle_listas != null
                                        && (recycle_listas.adapter as ListaSeriesAdapter)
                                                .getItemViewType((recycle_listas.adapter as ListaSeriesAdapter).itemCount - 1)
                                        != Constantes.BuscaConstants.AD) {
                                    (recycle_listas.adapter as ListaSeriesAdapter).addAd(nativeAd)
                                    (recycle_listas.adapter as ListaSeriesAdapter).addLoading(totalResults = it.totalResults)
                                }
                            }
                        }
                    }
                }, {
                    if (view != null) {
                        Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                    }
                })

        subscriptions.add(inscricao)
    }

    fun getListaTipo(): String? {

        when (abaEscolhida) {

            R.string.air_date -> return Api.TYPESEARCH.SERIE.semana

            R.string.today -> return Api.TYPESEARCH.SERIE.hoje

            R.string.populares -> return Api.TYPESEARCH.SERIE.popular

            R.string.top_rated -> return Api.TYPESEARCH.SERIE.melhores
        }
        return null
    }
}

