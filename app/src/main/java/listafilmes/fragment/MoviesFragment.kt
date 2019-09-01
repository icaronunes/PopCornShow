package listafilmes.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import domain.Api
import fragment.FragmentBase
import kotlinx.android.synthetic.main.fragment_list_medias.*
import listafilmes.adapter.ListaFilmesAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.Constantes
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.UtilsKt
import utils.UtilsKt.Companion.getIdiomaEscolhido
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class MoviesFragment : FragmentBase() {

    private var abaEscolhida: Int = 0
    private var pagina = 1
    private var totalPagina: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (activity?.intent?.action == null) {
                this.abaEscolhida = arguments?.getInt(Constantes.NAV_DRAW_ESCOLIDO)!!
            } else {
                this.abaEscolhida = Integer.parseInt(arguments?.getString(Constantes.NAV_DRAW_ESCOLIDO)!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_medias, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAdMob(adView)

        recycle_listas.apply {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this.layoutManager = layoutManager
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            addOnScrollListener(InfiniteScrollStaggeredListener({ }, { getListaFilmes() }, layoutManager))
            adapter = ListaFilmesAdapter(activity!!)
        }

        if (!UtilsApp.isNetWorkAvailable(context)) {
            txt_listas?.visibility = View.VISIBLE
            txt_listas?.text = getString(R.string.no_internet)
            snack()

        } else {
            getListaFilmes()
        }
    }

    fun getListaFilmes() {

        val inscricao = Api(context!!).buscaDeFilmes(getTipo(), pagina = pagina, local = getIdiomaEscolhido(context!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(2, TimeUnit.SECONDS)
                .subscribe({ listaFilmes ->
                    if (view != null) {
                        if (pagina == listaFilmes.page) {
                            (recycle_listas.adapter as ListaFilmesAdapter)
                                    .addFilmes(listaFilmes?.results, listaFilmes?.totalResults!!)
                            pagina = listaFilmes.page
                            totalPagina = listaFilmes.totalPages
                            ++pagina

                            UtilsKt.getAnuncio(context!!, 2) {
                                if (recycle_listas != null &&
                                        (recycle_listas.adapter as ListaFilmesAdapter).itemCount > 0 &&
                                        (recycle_listas.adapter as ListaFilmesAdapter)
                                                .getItemViewType((recycle_listas.adapter as ListaFilmesAdapter).itemCount - 1) != Constantes.BuscaConstants.AD)
                                    (recycle_listas.adapter as ListaFilmesAdapter).addAd(it, totalPagina)
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


    private fun snack() {
        Snackbar.make(frame_list_filme!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(context)) {
                        txt_listas?.visibility = View.GONE
                        getListaFilmes()
                    } else {
                        snack()
                    }
                }.show()
    }

    private fun getTipo(): String? {

        when (abaEscolhida) {

            R.string.now_playing -> return Api.TYPESEARCH.FILME.agora

            R.string.upcoming -> return Api.TYPESEARCH.FILME.chegando

            R.string.populares -> return Api.TYPESEARCH.FILME.popular

            R.string.top_rated -> return Api.TYPESEARCH.FILME.melhores

        }
        return ""
    }

}

