package fragment

import adapter.ProximosAdapter
import adapter.SeguindoRecycleAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import domain.Api
import domain.UserTvshow
import domain.tvshow.Tvshow
import kotlinx.coroutines.experimental.*
import utils.Constantes
import utils.UtilsApp
import java.io.Serializable
import java.util.*

/**
 * Created by icaro on 25/11/16.
 */
class ListaSeguindoFragment : Fragment() {

    private var userTvshows: MutableList<UserTvshow>? = null
    private var tipo: Int = 0
    private var recyclerViewMissing: RecyclerView? = null
    private var recyclerViewSeguindo: RecyclerView? = null
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
                //verificarProximoEp()
            }

            1 -> {
                verificarSerieCoroutine()
            }
        }
    }

    fun verificarSerieCoroutine() {
        userTvshows?.forEachIndexed { indexFire, tvFire ->
            try {
                rotina = GlobalScope.launch(Dispatchers.Main) {
                    val serie = async(Dispatchers.IO) { Api(context = context!!).getTvShowLiteC(tvFire.id) }.await()
                    if (serie.id == null) {
                        adapterSeguindo?.add(tvFire)
                        return@launch
                    }
                    if (serie.numberOfEpisodes != tvFire.numberOfEpisodes) {
                        tvFire.desatualizada = true
                        adapterSeguindo?.add(tvFire)

                    } else {
                        adapterSeguindo?.add(tvFire)
                    }
                }
            } catch (ex: Exception) {
                ex.message
            }
        }
    }

    private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
        Log.d("Icaro", "getViewMissing")
        val view = inflater.inflate(R.layout.temporadas, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
        adapterProximo = ProximosAdapter(this.activity, mutableListOf<UserTvshow>())
        recyclerViewMissing = view.findViewById<View>(R.id.temporadas_recycle) as RecyclerView
        recyclerViewMissing!!.setHasFixedSize(true)
        recyclerViewMissing!!.itemAnimator = DefaultItemAnimator()
        recyclerViewMissing!!.layoutManager = LinearLayoutManager(context)
        recyclerViewMissing!!.adapter = adapterProximo
        return view
    }

    private fun getViewSeguindo(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.seguindo, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
        recyclerViewSeguindo = view.findViewById<View>(R.id.seguindo_recycle) as RecyclerView
        adapterSeguindo = SeguindoRecycleAdapter(activity, mutableListOf<UserTvshow>())
        recyclerViewSeguindo!!.setHasFixedSize(true)
        recyclerViewSeguindo!!.itemAnimator = DefaultItemAnimator()
        recyclerViewSeguindo!!.layoutManager = GridLayoutManager(context, 4)
        recyclerViewSeguindo!!.adapter = adapterSeguindo
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
