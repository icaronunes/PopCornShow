package tvshow.fragment

import Layout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.firebase.database.DataSnapshot
import domain.UserSeasons
import domain.UserTvshow
import domain.fillAllUserEpTvshow
import domain.tvshow.Tvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tvshow.TemporadasAdapter
import tvshow.viewmodel.TvShowViewModel
import utils.Api
import utils.ConstFirebase.SEASONS
import utils.Constant
import utils.gone
import utils.kotterknife.bindArgument
import utils.makeToast
import utils.patternRecyler
import utils.setScrollInvisibleFloatMenu
import kotlin.coroutines.CoroutineContext

/**
 * Created by icaro on 23/08/16.
 */
class TvShowFragmentSeason(override val layout: Int = Layout.temporadas) : BaseFragment() {
	private val model: TvShowViewModel by lazy { createViewModel(TvShowViewModel::class.java) }
	private val color: Int by bindArgument(Constant.COLOR_TOP, R.color.primary)
	private var recyclerViewTemporada: RecyclerView? = null
	private var userTvshow: UserTvshow? = null
	private var progressBarTemporada: ProgressBar? = null
	private lateinit var series: Tvshow

	companion object {
		@JvmStatic
		fun newInstance(color: Int) = TvShowFragmentSeason().apply {
			arguments = Bundle().apply {
				putInt(Constant.COLOR_TOP, color)
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View = getViewSeasons(inflater, container)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observerTvshow()
	}

	private fun observerTvshow() {
		model.tvShow.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					series = it.result
					(recyclerViewTemporada?.adapter as TemporadasAdapter).addTvshow(series)
					observersSeason()
					observersStream()
					observerIsFallow()
					progressGone()
				}
				is Failure -> {
					ops()
					progressGone()
				}
				is Loading -> {
				}
			}
		})
	}

	private fun observerIsFallow() {
		model.isFallow.observe(viewLifecycleOwner, Observer { addSeguindo(it) })
	}

	private fun addSeguindo(it: Boolean) {
		if (isInitAdapter()) {
			(recyclerViewTemporada?.adapter as? TemporadasAdapter)?.addFallow(it)
		} else {
			initAdapter()
			(recyclerViewTemporada?.adapter as? TemporadasAdapter)?.addFallow(it)
		}
	}

	private fun observersSeason() {
		model.fallow.observe(viewLifecycleOwner, Observer {
			fillAdapter(it)
		})
	}

	private fun observersStream() {
		model.real.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					if (isInitAdapter()) {
						(recyclerViewTemporada?.adapter as TemporadasAdapter).addStream(it.result)
					} else {
						initAdapter()
						(recyclerViewTemporada?.adapter as TemporadasAdapter).addStream(it.result)
					}
				}
			}
		})
	}

	private fun initAdapter() {
		recyclerViewTemporada?.adapter =
			TemporadasAdapter(
				requireActivity(),
				color
			) { position, idSeason, numberSeason ->
				changeEps(position = position, idSeason = idSeason, numberSeason = numberSeason)
			}
	}

	private fun isInitAdapter() = recyclerViewTemporada?.adapter != null
	private fun getViewSeasons(inflater: LayoutInflater, container: ViewGroup?): View {
		return inflater.inflate(layout, container, false).apply {
			progressBarTemporada = findViewById(R.id.progress_temporadas)
			recyclerViewTemporada = findViewById<RecyclerView>(R.id.temporadas_recycler)
				.patternRecyler(false).apply {
					setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
				}
			initAdapter()
		}
	}

	private fun fillAdapter(dataSnapshot: DataSnapshot) {
		if (dataSnapshot.exists()) {
			try {
				if (view != null && isInitAdapter()) {
					userTvshow = dataSnapshot.getValue(UserTvshow::class.java)
					(recyclerViewTemporada?.adapter as TemporadasAdapter).addUserTvShow(userTvshow!!)
				}
			} catch (e: Exception) {
				requireActivity().makeToast(R.string.ops_seguir_novamente)
			}
		}
	}

	private fun isVisto(position: Int): Boolean {
		return if (userTvshow?.seasons != null) {
			if (userTvshow?.seasons?.getOrNull(position) != null) {
				return userTvshow?.seasons?.get(position)?.isVisto ?: false
			} else {
				false
			}
		} else {
			false
		}
	}

	private fun changeEps(position: Int, idSeason: Int, numberSeason: Int) {
		GlobalScope.launch(CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
			ops()
		}) {
			val season: UserSeasons = withContext(Dispatchers.Default) {
				Api(requireContext()).getTvSeasons(id = series.id, id_season = numberSeason)
			}.fillAllUserEpTvshow(
				userTvshow?.seasons?.find { it.id == idSeason },
				!isVisto(position)
			)
			val childUpdates = HashMap<String, Any>()
			childUpdates["${series.id}/desatualizada"] = true
			childUpdates["${series.id}/$SEASONS/$position"] = season
			model.fillSeason(childUpdates)
		}
	}

	private fun progressGone() {
		progressBarTemporada?.gone()
	}
}
