package fragment

import activity.BaseActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.io.Serializable
import java.util.ArrayList

import adapter.ProximosAdapter
import adapter.SeguindoRecycleAdapter
import android.util.Log
import br.com.icaro.filme.R
import domain.Api
import domain.FilmeService
import domain.UserSeasons
import domain.UserTvshow
import domain.tvshow.Tvshow
import info.movito.themoviedbapi.TmdbTvEpisodes
import kotlinx.coroutines.experimental.*
import utils.Constantes
import utils.UtilsApp
import java.util.HashMap
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by icaro on 25/11/16.
 */
class ListaSeguindoFragment : Fragment() {

    private val TAG = ListaSeguindoFragment::class.java.name
    private var userTvshows: MutableList<UserTvshow>? = null
    private var tipo: Int = 0
    private var recyclerViewMissing: RecyclerView? = null
    private var recyclerViewSeguindo: RecyclerView? = null
    private var eventListener: ValueEventListener? = null
    private var seguindoDataBase: DatabaseReference? = null
    private var rotina: Job? = null
    private var userTvshowNovo: UserTvshow? = null

    private var mAuth: FirebaseAuth? = null
    private var atualizarDatabase: FirebaseDatabase? = null

    private lateinit var adapter: ProximosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments!!.getInt(Constantes.ABA)
            userTvshows = arguments!!.getSerializable(Constantes.SEGUINDO) as MutableList<UserTvshow>
        }
        adapter = ProximosAdapter(this.activity, mutableListOf())
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        seguindoDataBase = database.getReference("users").child(mAuth.currentUser!!
                .uid).child("seguindo")
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
        verificarSerieCoroutine()
    }


    fun verificarSerieCoroutine() {
        userTvshows?.forEachIndexed { indexFire, tvFire ->
            rotina = GlobalScope.launch(Dispatchers.IO) {
                val serie = async { Api(context = context!!).getTvShowLiteC(tvFire.id) }.await()
                if (serie.numberOfEpisodes != tvFire.numberOfEpisodes) {
                    try {
                        tvFire.seasons?.forEachIndexed { index, userSeasons ->
                            if (userSeasons.userEps?.size != serie.seasons?.get(index)?.episodeCount) {
                                userTvshowNovo = UtilsApp.setUserTvShow(serie)
                                atualizarRealDate(indexFire, index, serie, tvFire)
                            }
                        }

                    } catch (ex: Exception) {
                        ex.message
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        adapter.add(tvFire)
                    }
                }
            }
        }

    }

    fun atualizarRealDate(indexSerie: Int, indexSeason: Int, serie: Tvshow?, userTvshow: UserTvshow) {

        GlobalScope.launch(Dispatchers.IO) {
            val tvSeasons = async { Api(context!!).getTvSeasonsC(id = userTvshow.id, id_season = userTvshow.numberOfSeasons) }.await()
            userTvshowNovo?.seasons?.get(indexSeason)?.userEps = UtilsApp.setEp2(tvSeasons)
            atulizarDataBase(indexSerie, indexSeason)

        }
    }

    private fun atulizarDataBase(indexSerie: Int, indexSeason: Int) {

        userTvshowNovo?.seasons?.get(indexSeason)?.seasonNumber = userTvshows!![indexSerie].seasons!![indexSeason].seasonNumber
        userTvshowNovo?.seasons?.get(indexSeason)?.isVisto = userTvshows!![indexSerie].seasons!![indexSeason].isVisto
        atulizarDataBaseEps(indexSerie, indexSeason)

    }

    private fun atulizarDataBaseEps(indexSerie: Int, indexSeason: Int) {

        for ((indexEp, userEp) in userTvshows?.get(indexSerie)?.seasons?.get(indexSeason)?.userEps?.withIndex()!!) {
            if (indexSeason <= userTvshowNovo?.seasons?.size!!)
                userTvshowNovo?.seasons
                        ?.get(indexSeason)
                        ?.userEps?.set(indexEp, userEp)
        }
        //usar outro metodo para validar
        if (userTvshowNovo?.seasons?.get(indexSeason)?.userEps?.size!! > userTvshows?.get(indexSerie)?.seasons?.get(indexSeason)!!.userEps?.size!!) {
            userTvshowNovo?.seasons?.get(indexSeason)?.isVisto = false
        }

        setDataBase(userTvshowNovo, indexSeason)
    }

    private fun setDataBase(userTvshowNovo: UserTvshow?, index: Int) {

        val childUpdates = HashMap<String, Any>().apply {
            put("/numberOfEpisodes", userTvshowNovo?.numberOfEpisodes!!) //TODO nao atualiza numero
            put("/numberOfSeasons", userTvshowNovo.numberOfSeasons)
            put("/poster", userTvshowNovo.poster!!)
            put("seasons/$index", userTvshowNovo.seasons!![index])
        }

        atualizarDatabase = FirebaseDatabase.getInstance()
        val myRef = atualizarDatabase?.getReference("users")
        myRef?.child(mAuth?.currentUser?.uid!!)
                ?.child("seguindo")
                ?.child(userTvshowNovo?.id.toString())
                ?.updateChildren(childUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isComplete) {
                        GlobalScope.launch(Dispatchers.Main) {
                            adapter.add(userTvshowNovo!!)
                            Toast.makeText(context, R.string.season_updated, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("TAG", task.exception.toString())
                    }
                }
    }


    private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
        Log.d("Icaro", "getViewMissing")
        val view = inflater.inflate(R.layout.temporadas, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
       // adapter = ProximosAdapter(activity, mutableListOf<UserTvshow>())
        recyclerViewMissing = view.findViewById<View>(R.id.temporadas_recycle) as RecyclerView
        recyclerViewMissing!!.setHasFixedSize(true)
        recyclerViewMissing!!.itemAnimator = DefaultItemAnimator()
        recyclerViewMissing!!.layoutManager = LinearLayoutManager(context)
        recyclerViewMissing!!.adapter = adapter
        return view
    }

    private fun getViewSeguindo(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.seguindo, container, false) // Criar novo layout
        view.findViewById<View>(R.id.progressBarTemporadas).visibility = View.GONE
        recyclerViewSeguindo = view.findViewById<View>(R.id.seguindo_recycle) as RecyclerView
        recyclerViewSeguindo!!.setHasFixedSize(true)
        recyclerViewSeguindo!!.itemAnimator = DefaultItemAnimator()
        recyclerViewSeguindo!!.layoutManager = GridLayoutManager(context, 4)
        if (userTvshows!!.size > 0) {
            recyclerViewSeguindo!!.adapter = SeguindoRecycleAdapter(activity, userTvshows)
        } else {
            view.findViewById<View>(R.id.text_search_empty).visibility = View.VISIBLE
            (view.findViewById<View>(R.id.text_search_empty) as TextView).setText(R.string.empty)
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (eventListener != null) {
            seguindoDataBase!!.removeEventListener(eventListener!!)
        }

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
