package lista.tvshow.fragment

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
import lista.viewmodel.ListByTypeViewModel
import lista.tvshow.adapter.ListaSeriesAdapter
import utils.Constant
import utils.InfiniteScrollStaggeredListener
import utils.UtilsApp
import utils.gone
import utils.makeToast
import utils.resolver
import utils.visible

/**
 * Created by icaro on 14/09/16.
 */
class TvShowsFragment(override val layout: Int = R.layout.fragment_list_medias) : BaseFragment() {
	val model: ListByTypeViewModel by lazy { createViewModel(ListByTypeViewModel::class.java) }
	private lateinit var abaEscolhida: String
	private var pagina = 1
	private var listAd: MutableList<UnifiedNativeAd> = mutableListOf()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			this.abaEscolhida = requireArguments().getString(Constant.NAV_DRAW_ESCOLIDO, "")
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAdMob(adView)
		observers()
		observerAd()
		setupRecycler()

		if (!UtilsApp.isNetWorkAvailable(requireContext())) {
			txt_listas.visibility = View.VISIBLE
			txt_listas.text = resources.getString(R.string.no_internet)
			snack()
		} else {
			fetchListTv()
		}

	}

	override fun onStart() {
		super.onStart()
		model.fillAdNative()
	}

	private fun setupRecycler() {
		recycle_listas.apply {
			val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			this.layoutManager = layoutManager
			itemAnimator = DefaultItemAnimator()
			addOnScrollListener(InfiniteScrollStaggeredListener({},
				{ fetchListTv() },
				layoutManager))
			setHasFixedSize(true)
			adapter = ListaSeriesAdapter(context)
		}
	}

	private fun observers() {
		model.tvshows.observe(viewLifecycleOwner, Observer {
			it.resolver(requireActivity(),
				successBlock = { list ->
						if (pagina == list.page) {
							val listWithAd = list.results + ListAd.createList(listAd.take(4))
							(recycle_listas.adapter as ListaSeriesAdapter).addItems(listWithAd)
							pagina = list.page
							++pagina
							model.fillAdNative()
						}
						model.setLoadingTv(false)
				},
				failureBlock = {
					requireActivity().makeToast(R.string.ops)
					model.setLoadingTv(false)
				},
				loadingBlock = { status ->
					loading(status)
				},
				genericError = ErrorTryDefault(requireActivity()) {
					fetchListTv()
				})
		})
	}

	private fun observerAd() {
		model.ads.observe(viewLifecycleOwner, Observer {
			it?.let { ads ->
				listAd = ads
			}
		})
	}

	fun loading(loading: Boolean) {
		if (loading) progress_horizontal.visible() else progress_horizontal.gone()
	}

	private fun snack() {
		Snackbar.make(frame_list_filme, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(requireContext())) {
					txt_listas.visibility = View.INVISIBLE
					fetchListTv()
				} else {
					snack()
				}
			}.show()
	}

	private fun fetchListTv() {
		model.fetchListTvshow(abaEscolhida, pagina)
	}
}
