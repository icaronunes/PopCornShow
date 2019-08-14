package fragment

import adapter.ProximosAdapter
import adapter.SeguindoRecycleAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import domain.Api
import domain.UserEp
import domain.UserTvshow
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.apache.commons.lang3.tuple.MutablePair
import utils.Constantes
import java.io.Serializable

/**
 * Created by icaro on 25/11/16.
 */
class ListaSeguindoFragment : Fragment() {

    private var userTvshows: MutableList<UserTvshow>? = null
    private var tipo: Int = 0
    private var recyclerViewMissing: ShimmerRecyclerView? = null
    private var recyclerViewSeguindo: ShimmerRecyclerView? = null
    private var rotina: Job? = null
    private var adapterProximo: ProximosAdapter? = null
    private var adapterSeguindo: SeguindoRecycleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments!!.getInt(Constantes.ABA)
            userTvshows = arguments!!.getSerializable(Constantes.SEGUINDO) as MutableList<UserTvshow>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        when (tipo) {

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
        Log.d("Icaro", "onViewCreated")
        when (tipo) {
            0 -> {
                verificarProximoEp()
            }

            1 -> {
                verificarSerieCoroutine()
            }
        }
    }

    private fun verificarProximoEp() {
        val series = mutableListOf<Pair<UserEp, UserTvshow>>()
        userTvshows?.forEach eps@{ userTvshow ->
            userTvshow.seasons?.forEach {
                if (it.seasonNumber != 0)
                    it.userEps?.forEach { userEp ->
                        if (!userEp.isAssistido) {
                            val pair = Pair(userEp, userTvshow)
                            series.add(pair)
                            adapterProximo?.addSeries(pair)
                            return@eps
                        }
                    }
            }
        }
        atualizar(series)
    }


    private fun atualizar(series: MutableList<Pair<UserEp, UserTvshow>>) {

        series.forEach {
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    //Pegar serie atualizada do Server. Enviar para o metodo para atualizar baseado nela
                    val serie = async(Dispatchers.IO) { Api(context = context!!).getTvShowLiteC(it.second.id) }
                    val ep = async(Dispatchers.IO) { Api(context = context!!).getTvShowEpC(it.second.id, it.first.seasonNumber, it.first.episodeNumber) }
                    val ultima = async {  MutablePair(ep.await(), serie.await()) }.await()
                    launch {
                        adapterProximo?.addAtual(ultima)
                    }
                }
            } catch (ex: Exception) {
                Log.d(this.javaClass.name, ex.toString())
            }
        }
        //(recyclerViewMissing as ShimmerRecyclerView).hideShimmerAdapter()
    }

    fun verificarSerieCoroutine() {

        userTvshows?.forEach { tvFire ->
            try {
                adapterSeguindo?.add(tvFire)
                rotina = GlobalScope.launch(Dispatchers.IO) {
                    val serie = async {
                        delay(600)
                        Api(context = context!!).getTvShowLiteC(tvFire.id)
                    }.await()
                    if (serie.numberOfEpisodes != tvFire.numberOfEpisodes) {
                        launch(Main) {
                            tvFire.desatualizada = true
                            adapterSeguindo?.addAtualizado(tvFire)
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.message
            }
        }
    }

    private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.temporadas, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
        adapterProximo = ProximosAdapter(requireActivity())
       // recyclerViewMissing = view.findViewById<ShimmerRecyclerView>(R.id.temporadas_recycle)
       // (recyclerViewMissing as ShimmerRecyclerView).showShimmerAdapter()
//        recyclerViewMissing?.setHasFixedSize(true)
//        recyclerViewMissing?.itemAnimator = DefaultItemAnimator()
//        recyclerViewMissing?.layoutManager = LinearLayoutManager(context)
//        recyclerViewMissing?.adapter = adapterProximo
        return view
    }

    private fun getViewSeguindo(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.seguindo, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
        //recyclerViewSeguindo = view.findViewById<View>(R.id.seguindo_recycle) as RecyclerView
      //  adapterSeguindo = SeguindoRecycleAdapter(activity, mutableListOf<UserTvshow>())
//        recyclerViewSeguindo?.setHasFixedSize(true)
//        recyclerViewSeguindo?.itemAnimator = DefaultItemAnimator()
//        recyclerViewSeguindo?.layoutManager = GridLayoutManager(context, 4)
//        recyclerViewSeguindo?.adapter = adapterSeguindo
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rotina != null) {
            rotina!!.cancel()
        }
    }

    companion object {

        fun newInstance(tipo: Int, userTvshows: List<UserTvshow>): Fragment {
            val fragment = ListaSeguindoFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constantes.SEGUINDO, userTvshows as Serializable)
            bundle.putInt(Constantes.ABA, tipo)
            fragment.arguments = bundle

            return fragment
        }
    }
}
