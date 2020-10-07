package listafilmes.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.snackbar.Snackbar
import domain.movie.ListAd
import error.ErrorTryDefault
import kotlinx.android.synthetic.main.fragment_list_medias.adView
import kotlinx.android.synthetic.main.fragment_list_medias.frame_list_filme
import kotlinx.android.synthetic.main.fragment_list_medias.recycle_listas
import kotlinx.android.synthetic.main.fragment_list_medias.txt_listas
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import listafilmes.adapter.ListaFilmesAdapter
import listafilmes.viewmodel.ListByTypeViewModel
import utils.Constant
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.gone
import utils.makeToast
import utils.resolver
import utils.visible

class MoviesFragment(override val layout: Int = R.layout.fragment_list_medias) : BaseFragment() {
	private lateinit var abaEscolhida: String
	private var pagina = 1
	private var totalPagina: Int = 0
	private var listAd: MutableList<UnifiedNativeAd> = mutableListOf()
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
		setupRecycler()

		if (!UtilsApp.isNetWorkAvailable(requireContext())) {
			txt_listas?.visibility = View.VISIBLE
			txt_listas?.text = getString(R.string.no_internet)
			snack()
		} else {
			fetchList()
		}
		observerAd()
		observerMovies()
	}

	private fun setupRecycler() {
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
	}

	override fun onStart() {
		super.onStart()
		model.fillAdNative()
	}

	private fun observerMovies() {
		model.movies.observe(viewLifecycleOwner, Observer {
			it.resolver(requireActivity(), successBlock = { list ->
				if (pagina == list.page) {
					val listWithAd = list.results + ListAd.createList(listAd.take(4))
					(recycle_listas.adapter as ListaFilmesAdapter)
						.addItems(listWithAd, list.totalResults)

					pagina = list.page
					totalPagina = list.totalPages
					++pagina
					model.fillAdNative()
				}
				model.setLoadingMovie(false)
			},
				failureBlock = {
					requireActivity().makeToast(R.string.ops)
					model.setLoadingMovie(false)
				},
				loadingBlock = { statusLoading -> loading(loading = statusLoading) },
				genericError = ErrorTryDefault(requireActivity())
				{ fetchList() }
			)
		})
	}

	private fun observerAd() {
		model.ads.observe(viewLifecycleOwner, Observer {
			it?.let { ads ->
				listAd = ads
			}
		})
	}

	private fun fetchList() {
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
