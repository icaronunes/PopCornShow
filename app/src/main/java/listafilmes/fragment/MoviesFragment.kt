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
import fragment.FragmentBase
import kotlinx.android.synthetic.main.fragment_list_medias.adView
import kotlinx.android.synthetic.main.fragment_list_medias.frame_list_filme
import kotlinx.android.synthetic.main.fragment_list_medias.recycle_listas
import kotlinx.android.synthetic.main.fragment_list_medias.txt_listas
import listafilmes.adapter.ListaFilmesAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.Api
import utils.Constant
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.UtilsKt
import utils.UtilsKt.Companion.getIdiomaEscolhido
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class MoviesFragment : FragmentBase() {

    private lateinit var abaEscolhida: String
    private var pagina = 1
    private var totalPagina: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
                this.abaEscolhida = arguments?.getString(Constant.NAV_DRAW_ESCOLIDO)
                    ?: getString(R.string.now_playing)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            adapter = ListaFilmesAdapter(requireActivity())
        }

        if (!UtilsApp.isNetWorkAvailable(requireContext())) {
            txt_listas?.visibility = View.VISIBLE
            txt_listas?.text = getString(R.string.no_internet)
            snack()
        } else {
            getListaFilmes()
        }
    }

    fun getListaFilmes() {

        val inscricao = Api(requireContext()).buscaDeFilmes(abaEscolhida, pagina = pagina, local = getIdiomaEscolhido(requireContext()))
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

                        UtilsKt.getAnuncio(requireContext(), 2) {
                            if (recycle_listas != null &&
	                            (recycle_listas.adapter as ListaFilmesAdapter).itemCount > 0 &&
	                            (recycle_listas.adapter as ListaFilmesAdapter)
		                            .getItemViewType((recycle_listas.adapter as ListaFilmesAdapter).itemCount - 1) != Constant.ViewTypesIds.AD
                            )
	                            (recycle_listas.adapter as ListaFilmesAdapter).addAd(
		                            it,
		                            totalPagina
	                            )
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
                if (UtilsApp.isNetWorkAvailable(requireContext())) {
                    txt_listas?.visibility = View.GONE
                    getListaFilmes()
                } else {
                    snack()
                }
            }.show()
    }
}
