package seguindo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import br.com.icaro.filme.R.layout
import domain.UserEp
import domain.UserTvshow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.Api
import utils.Constant
import kotlin.coroutines.CoroutineContext

/**
 * Created by icaro on 25/11/16.
 */
class ListFollowFragment2 : BaseFragment() {

	private lateinit var progress: ProgressBar
	private lateinit var recycler: RecyclerView
	private var userTvshows: MutableList<UserTvshow>? = null
	private var position: Int = 0
	private var rotina: Job? = null
	private var adapterProximo: ProximosAdapter? = null
	private var adapterSeguindo: SeguindoRecycleAdapter? = null

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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			position = requireArguments().getInt(Constant.ABA)
			userTvshows = mutableListOf()
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		when (position) {

			0 -> {
				return getViewMissing(inflater, container)
			}
			1 -> {
				return getViewSeguindo(inflater, container)
			}
		}
		return null
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		when (position) {
			0 -> {
				verificarProximoEp()
			}
			1 -> {
				verificarSerieCoroutine()
			}
		}
	}

	private fun verificarProximoEp() {
//		val series = mutableListOf<Pair<UserEp, UserTvshow>>()
//		userTvshows?.forEach eps@{ userTvshow ->
//			userTvshow.seasons.forEach {
//				if (it.seasonNumber != 0)
//					it.userEps.forEach { userEp ->
//						if (!userEp.isAssistido) {
//							val pair = Pair(userEp, userTvshow)
//							series.add(pair)
//							adapterProximo?.addSeries(pair)
//							return@eps
//						}
//					}
//			}
//		}
//		atualizar(series)
	}

	private fun atualizar(series: MutableList<Pair<UserEp, UserTvshow>>) {
//		series.forEach {
//			try {
//				GlobalScope.launch(coroutineContext) {
//					// Pegar serie atualizada do Server. Enviar para o metodo para atualizar baseado nela
//					val serie =
//						async(IO) { Api(context = requireContext()).getTvShowLiteC(it.second.id) }
//					val ep = async(IO) {
//						Api(context = requireContext()).getTvShowEpC(
//							it.second.id,
//							it.first.seasonNumber,
//							it.first.episodeNumber
//						)
//					}
//					val ultima = async { MutablePair(ep.await(), serie.await()) }.await()
//					launch {
//						adapterProximo?.add(ultima)
//					}
//				}
//			} catch (ex: Exception) {
//				Log.d(this.javaClass.name, ex.toString())
//			}
//		}
	}

	private fun verificarSerieCoroutine() {
		try {
			userTvshows?.forEach { tvFire ->
				rotina = GlobalScope.launch(coroutineContext) {
					val serie = async(IO) {
						delay(600)
						Api(context = requireContext()).getTvShowLiteC(tvFire.id)

					}.await()
					if (serie.numberOfEpisodes != null) {
						tvFire.desatualizada = serie.numberOfEpisodes != tvFire.numberOfEpisodes
						adapterSeguindo?.add(tvFire)
					} else {
						tvFire.desatualizada = false
						adapterSeguindo?.add(tvFire)
					}
				}
			}
		} catch (ex: Exception) {
			ex.message
		}
	}

	private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
		val view = inflater.inflate(R.layout.temporadas, container, false)
		view.findViewById<View>(R.id.progress_temporadas).visibility = View.GONE
		adapterProximo = ProximosAdapter()
		view.findViewById<RecyclerView>(R.id.temporadas_recycler).apply {
			setHasFixedSize(true)
			itemAnimator = DefaultItemAnimator()
			layoutManager = LinearLayoutManager(context)
//			adapter = adapterProximo
		}
		return view
	}

	private fun getViewSeguindo(inflater: LayoutInflater, container: ViewGroup?): View {
		return inflater.inflate(layout.seguindo, container, false).apply {
			progress = findViewById(R.id.progress_temporadas)
			adapterSeguindo = SeguindoRecycleAdapter(activity)
			recycler = findViewById<RecyclerView>(R.id.seguindo_recycle).apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager = GridLayoutManager(this@ListFollowFragment2.context, 4)
				adapter = adapterSeguindo
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if (rotina != null) {
			rotina!!.cancel()
		}
	}

	companion object {
		fun newInstance(tipo: Int): Fragment {
			return ListFollowFragment2().apply {
				arguments = Bundle().apply {
//					putSerie)alizable(Constant.SEGUINDO, userTvshows as Serializabl
					putInt(Constant.ABA, tipo)
				}
			}
		}
	}
}
