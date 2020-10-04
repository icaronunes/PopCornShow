package lista.movie.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_list_medias.adView
import kotlinx.android.synthetic.main.fragment_list_medias.frame_list_filme
import kotlinx.android.synthetic.main.fragment_list_medias.recycle_listas
import kotlinx.android.synthetic.main.fragment_list_medias.txt_listas
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import lista.movie.adapter.ListaFilmesAdapter
import lista.viewmodel.ListByTypeViewModel
import utils.Constant
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.UtilsKt
import utils.gone
import utils.makeToast
import utils.visible

/**
 * A simple [Fragment] subclass.
 */
class MoviesFragment(override val layout: Int = R.layout.fragment_list_medias) : BaseFragment() {
	private lateinit var abaEscolhida: String
	private var pagina = 1
	private var totalPagina: Int = 0
	val model: ListByTypeViewModel by lazy { createViewModel(ListByTypeViewModel::class.java) }
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			this.abaEscolhida = arguments?.getString(Constant.NAV_DRAW_ESCOLIDO)
				?: getString(R.string.now_playing)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
        setAdMob(adView)

        recycle_listas.apply {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this.layoutManager = layoutManager
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            addOnScrollListener(InfiniteScrollStaggeredListener({ },
                { fetchList() },
                layoutManager))
            adapter = ListaFilmesAdapter(requireActivity())
        }

        if (!UtilsApp.isNetWorkAvailable(requireContext())) {
            txt_listas?.visibility = View.VISIBLE
            txt_listas?.text = getString(R.string.no_internet)
            snack()
        } else {
            fetchList()
        }
        observerMovies()
	}

	private fun observerMovies() {
		model.movies.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Success -> {
                    val list = it.result
                    if (pagina == list.page) {
                        (recycle_listas.adapter as ListaFilmesAdapter)
                            .addFilmes(list.results, list.totalResults)
                        pagina = list.page
                        totalPagina = list.totalPages
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
	                    model.setLoadingMovie(false)
                    }
                }
                is Failure -> {
	                requireActivity().makeToast(R.string.ops)
	                model.setLoadingMovie(false)
                }
                is Loading -> { loading(it.loading) }
            }
        })
	}

	fun fetchList() {
		model.fetchListMovies(abaEscolhida, pagina)
	}

	private fun snack() {
		Snackbar.make(frame_list_filme!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(requireContext())) {
					txt_listas?.visibility = View.GONE
					fetchList()
				} else {
					snack()
				}
			}.show()
	}

	fun loading(loading: Boolean) {
		if (loading) progress_horizontal.visible() else progress_horizontal.gone()
	}
}
