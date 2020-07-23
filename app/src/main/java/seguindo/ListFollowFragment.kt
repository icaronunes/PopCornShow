package seguindo

import Layout
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.google.firebase.database.DataSnapshot
import domain.EpisodesItem
import domain.UserEp
import domain.UserTvshow
import domain.fistNotWatch
import domain.tvshow.Fallow
import domain.tvshow.Tvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import utils.Api
import utils.gone
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * Created by icaro on 25/11/16.
 */
class ListFollowFragment : BaseFragment() {

	private lateinit var progress: ProgressBar
	private var fallowAdapterDelegates: FallowAdapterDelegates? = null

	private var listTvShow: MutableList<Tvshow> = mutableListOf()
	private var listEp: MutableList<EpisodesItem> = mutableListOf()

	private val model: FallowModel by lazy { createViewModel(FallowModel::class.java) }

	private val coroutineContext: CoroutineContext
		get() = Main + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
			Handler(Looper.getMainLooper()).post {
				Toast
					.makeText(
						requireActivity(),
						requireActivity().getString(R.string.ops), Toast.LENGTH_LONG
					).show()
			}
		}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return getViewMissing(inflater, container)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observers()
	}

	private fun observers() {
		model.fallow.observe(viewLifecycleOwner, Observer {
			setDataFallow(it)
		})
	}

	private fun setDataFallow(dataSnapshot: DataSnapshot) {
		val userTvshowFire = ArrayList<UserTvshow>()
		if (dataSnapshot.exists()) {
			try {
				dataSnapshot.children
					.asSequence()
					.map {
						it.getValue(UserTvshow::class.java)
					}
					.forEach { userTvshowFire.add(it!!) }
			} catch (e: Exception) {
				Log.d(this.javaClass.name, e.toString())
				Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
			}
		}
		val notWatch = userTvshowFire.mapNotNull {
			val ep = it.fistNotWatch()
			if (ep == null) null else Pair(it, ep)
		}
		fetchMedia(notWatch)
	}

	private fun fetchMedia(series: List<Pair<UserTvshow, UserEp>>) {
		fallowAdapterDelegates?.clearList()
		series.forEach { pair ->
			val (tvShow, epUser) = pair
			GlobalScope.launch(coroutineContext) {
				val tvShowUpdated = listTvShow.find { it.id == tvShow.id } ?: async(IO) {
					Api(
						context = requireContext()
					).getTvShowLiteC(tvShow.id)

				}.await()
				listTvShow.add(tvShowUpdated)
				val ep = listEp.find { it.id == epUser.id } ?: async(IO) {
					Api(context = requireContext()).getTvShowEpC(
						tvShow.id,
						epUser.seasonNumber,
						epUser.episodeNumber
					)
				}.await()
				listEp.add(ep)
				val updater = Fallow(tvShowUpdated, tvShow, ep)
				//Todo enviar atualização pro firebase - chamar no destroy se possivel
				fallowAdapterDelegates?.add(updater)
			}
		}
		progress.gone()
	}

	private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
		return with(inflater.inflate(Layout.temporadas, container, false)) {
			progress = findViewById(R.id.progress_temporadas)
			fallowAdapterDelegates = FallowAdapterDelegates(context)
			findViewById<RecyclerView>(R.id.temporadas_recycler).apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager = LinearLayoutManager(context)
				adapter = fallowAdapterDelegates
			}
		}
	}
}
