package main

import adapter.MovieMainAdapter
import adapter.TvShowMainAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.FilmeApplication
import applicaton.PopCornViewModelFactory
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.filmes_main
import domain.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.android.synthetic.main.filmes_main.*
import kotlinx.android.synthetic.main.tvshow_main.*
import kotlinx.coroutines.*
import listafilmes.activity.FilmesActivity
import listaserie.activity.TvShowsActivity
import utils.Constantes
import utils.UtilsApp
import java.net.ConnectException


/**
 * Created by icaro on 23/08/16.
 */
class MainFragment : Fragment() {
    private var rotina: Job = Job()
    private var tipo: Int = 0

    companion object {

        fun newInstance(informacoes: Int): Fragment {
            val fragment = MainFragment()
            val bundle = Bundle()
            bundle.putInt(Constantes.ABA, informacoes)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments!!.getInt(Constantes.ABA)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (tipo == R.string.tvshow_main) {
            setScrollTvshowButton()
        } else {
            setScrollFilmeButton()
        }
        val model = createViewModel(MainFragViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        when (tipo) {
            filmes_main -> {
                return getViewMovie(inflater, container)
            }
            R.string.tvshow_main -> {
                return getViewTvshow(inflater, container)
            }
        }
        return null
    }

    fun createViewModel(java: Class<MainFragViewModel>): MainFragViewModel {
        val factory = PopCornViewModelFactory(application = this.activity?.application as FilmeApplication)
        return ViewModelProviders.of(this, factory).get(java)
    }

    private fun setScrollFilmeButton() {
        chip_group_movie.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_now_playing -> {
                    startActivity(Intent(activity, FilmesActivity::class.java).apply {
                        putExtra(Constantes.ABA, R.string.now_playing)
                        putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing)
                    })
                }

                R.id.chip_upcoming -> {
                    startActivity(Intent(activity, FilmesActivity::class.java).apply {
                        putExtra(Constantes.ABA, R.string.upcoming)
                        putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.upcoming)
                    })
                }

                R.id.chip_populares -> {
                    startActivity(Intent(activity, FilmesActivity::class.java).apply {
                        putExtra(Constantes.ABA, R.string.populares)
                        putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.populares)
                    })
                }

                R.id.chip_top_rated -> {
                    startActivity(Intent(activity, FilmesActivity::class.java).apply {
                        putExtra(Constantes.ABA, R.string.top_rated)
                        putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.top_rated)
                    })
                }
            }
        }
    }

    private fun setScrollTvshowButton() {
        chip_group_tvshow.setOnCheckedChangeListener { chipGroup, id ->
            Log.d(this.javaClass.name, "$chipGroup - $id")
            when (id) {
                R.id.chip_air_data -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.air_date)
                    startActivity(intent)
                }
                R.id.chip_today -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.today)
                    startActivity(intent)
                }
                R.id.chip_populares_tvshow -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.populares)
                    startActivity(intent)
                }

                R.id.chip_top_rated_tvshow -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.top_rated)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getViewMovie(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.filmes_main, container, false)
        inflaterRecyclerMovie(view)

        if (UtilsApp.isNetWorkAvailable(context!!)) {
            setMoviePopular()
            setUpComing()
        } else {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun inflaterRecyclerMovie(view: View) {
        view.findViewById<RecyclerView>(R.id.recycle_movie_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        view.findViewById<RecyclerView>(R.id.recycle_movieontheair_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpComing() {
       rotina = GlobalScope.launch(Dispatchers.Main) {
            try {
                val upComing = async(Dispatchers.Default) {
                    Api(context!!).getUpcoming()
                }
                setScrollMovieOntheAir(upComing.await())
            } catch (ex: ConnectException) {
                Log.d(this.javaClass.name, "ERRO - ConnectException FRAG ${ex.message}")
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                rotina.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                Log.d(this.javaClass.name, "ERRO - Exception FRAG ${ex.message}")
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                rotina.cancelAndJoin()
            }
        }
    }

    private fun setMoviePopular() {
       rotina = GlobalScope.launch(Dispatchers.Main) {
            try {
                val popular = async(Dispatchers.IO) {
                    Api(context!!).getMoviePopular()
                }
                setScrollMoviePopular(popular.await())
            } catch (ex: ConnectException) {
                Log.d(this.javaClass.name, "ERRO - ConnectException FRAG ${ex.message}")
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                rotina.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                Log.d(this.javaClass.name, "ERRO - Exception FRAG ${ex.message}")
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                rotina.cancelAndJoin()
            }
        }
    }

    private fun getViewTvshow(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.tvshow_main, container, false)
        inflaterRecyclerTv(view)
        if (UtilsApp.isNetWorkAvailable(context!!)) {
            setPopularTv()
            setAiringToday()
        } else {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun setAiringToday() {
       rotina =  GlobalScope.launch(Dispatchers.Main) {
            try {
                val airTv = async(Dispatchers.IO) {
                    Api(context!!).getAiringToday()
                }
                setScrollTvShowToDay(airTv.await())
            } catch (ex: ConnectException) {
                rotina.cancelAndJoin()
                Log.d(this.javaClass.name, "ERRO - ConnectException FRAG${ex.message}")
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
            } catch (ex: java.lang.Exception) {
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                Log.d(this.javaClass.name, "ERRO - Exception FRAG${ex.message}")
                rotina.cancelAndJoin()
            }
        }
    }

    private fun setPopularTv() {
      rotina = GlobalScope.launch(Dispatchers.Main) {
            try {
                val popular = async(Dispatchers.Default) {
                    Api(context!!).getPopularTv()
                }
                setScrollTvShowPopulares(popular.await())
            } catch (ex: ConnectException) {
                rotina.cancelAndJoin()
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
            } catch (ex: java.lang.Exception) {
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                rotina.cancelAndJoin()
            }
        }
    }

    private fun inflaterRecyclerTv(view: View) {
        view.findViewById<RecyclerView>(R.id.tvshow_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        view.findViewById<RecyclerView>(R.id.recycle_tvshowtoday_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setScrollTvShowToDay(toDay: ListaSeries) {
        recycle_tvshowtoday_main.adapter = TvShowMainAdapter(activity, toDay)
    }

    private fun setScrollTvShowPopulares(popularTvShow: ListaSeries) {
        tvshow_popular_main.adapter = TvShowMainAdapter(activity, popularTvShow)
    }

    private fun setScrollMoviePopular(popular: ListaFilmes) {
        recycle_movieontheair_main.adapter = MovieMainAdapter(activity, popular)
    }

    private fun setScrollMovieOntheAir(airDay: ListaFilmes) {
        recycle_movie_popular_main.adapter = MovieMainAdapter(activity, airDay)
    }

    override fun onDestroy() {
        if (rotina.isActive) rotina.cancel()
        super.onDestroy()
    }
}
