package lista.tvshow.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.snackbar.Snackbar
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
import utils.UtilsKt
import utils.gone
import utils.makeToast
import utils.visible

/**
 * Created by icaro on 14/09/16.
 */
class TvShowsFragment(override val layout: Int = R.layout.fragment_list_medias) : BaseFragment() {
	val model: ListByTypeViewModel by lazy { createViewModel(ListByTypeViewModel::class.java) }
	private lateinit var abaEscolhida: String
	private var pagina = 1
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

		if (!UtilsApp.isNetWorkAvailable(requireContext())) {
			txt_listas.visibility = View.VISIBLE
			txt_listas.text = resources.getString(R.string.no_internet)
			snack()
		} else {
			fetchListTv()
		}
	}

	private fun observers() {
		model.tvshows.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Success -> {
                    val list = it.result
                    if (pagina == list.page) {
                        if (pagina == list.page) {
                            (recycle_listas.adapter as ListaSeriesAdapter).addSeries(list.results)
                            pagina = list.page
                            ++pagina
                            UtilsKt.getAnuncio(requireContext(), 3) { nativeAd: UnifiedNativeAd ->
                                if (recycle_listas != null &&
                                    (recycle_listas.adapter as ListaSeriesAdapter)
                                        .getItemViewType((recycle_listas.adapter as ListaSeriesAdapter).itemCount - 1)
                                    != Constant.ViewTypesIds.AD
                                ) {
                                    (recycle_listas.adapter as ListaSeriesAdapter).addAd(nativeAd)
                                    (recycle_listas.adapter as ListaSeriesAdapter).addLoading(
                                        totalResults = list.totalResults
                                    )
                                }
                            }
                        }
                        model.setLoadingTv(false)
                    }
                }
                is Failure -> {
                    requireActivity().makeToast(R.string.ops)
                    model.setLoadingTv(false)
                }
                is Loading -> {
                    loading(it.loading)
                }
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
