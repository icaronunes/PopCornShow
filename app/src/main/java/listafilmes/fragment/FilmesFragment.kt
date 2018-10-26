package listafilmes.fragment


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.firebase.analytics.FirebaseAnalytics
import domain.Api
import fragment.FragmentBase
import kotlinx.android.synthetic.main.fragment_list_filme.*
import listafilmes.adapter.ListaFilmesAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.Constantes
import utils.InfiniteScrollListener
import utils.UtilsApp
import utils.getIdiomaEscolhido


/**
 * A simple [Fragment] subclass.
 */
class FilmesFragment : FragmentBase() {

    private var abaEscolhida: Int = 0
    private var pagina = 1
    private var totalPagina: Int? = 0
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (activity?.intent?.action == null) {
                this.abaEscolhida = arguments?.getInt(Constantes.NAV_DRAW_ESCOLIDO)!!
            } else {
                this.abaEscolhida = Integer.parseInt(arguments?.getString(Constantes.NAV_DRAW_ESCOLIDO))
            }
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_filme, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycle_listas.apply {
            val gridLayout = GridLayoutManager(activity, 2)
            layoutManager = gridLayout
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(InfiniteScrollListener({ getListaFilmes() }, gridLayout))
            setHasFixedSize(true)
            adapter = ListaFilmesAdapter(activity!!)
        }

        if (!UtilsApp.isNetWorkAvailable(context)) {
            txt_listas?.visibility = View.VISIBLE
            txt_listas?.text = "SEM INTERNET"
            snack()

        } else {
            getListaFilmes()
        }
    }


    fun getListaFilmes() {

        val inscricao = Api(context!!).buscaDeFilmes(getTipo(), pagina = pagina, local = getIdiomaEscolhido(context!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (view != null) {
                        (recycle_listas.adapter as ListaFilmesAdapter).addFilmes(it?.results, it?.totalResults!!)
                        pagina = it.page!!
                        totalPagina = it.totalPages!!
                        ++pagina
                    }
                }, { erro ->
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
                        txt_listas?.visibility = View.INVISIBLE
                        getListaFilmes()
                    } else {
                        snack()
                    }
                }.show()
    }

    fun getTipo(): String? {

        when (abaEscolhida) {

            R.string.now_playing -> return Api.TIPOBUSCA.FILME.agora

            R.string.upcoming -> {
                return Api.TIPOBUSCA.FILME.chegando
            }

            R.string.populares -> {
                return Api.TIPOBUSCA.FILME.popular
            }

            R.string.top_rated -> {
                return Api.TIPOBUSCA.FILME.melhores
            }
        }
        return null
    }

}

