package fragment

import adapter.MovieMainAdapter
import adapter.TvShowMainAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.filmes_main
import com.google.android.material.chip.Chip
import domain.Api
import domain.FilmeService
import domain.ListaSeries
import domain.movie.ListaFilmes
import domain.movie.ListaItemFilme
import info.movito.themoviedbapi.TvResultsPage
import info.movito.themoviedbapi.model.core.MovieResultsPage
import kotlinx.android.synthetic.main.filmes_main.*
import kotlinx.android.synthetic.main.tvshow_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import listafilmes.activity.FilmesActivity
import listaserie.activity.TvShowsActivity
import utils.Constantes
import utils.UtilsApp
import utils.UtilsApp.getTimezone
import utils.getIdiomaEscolhido
import java.util.*
import java.util.Arrays.asList


/**
 * Created by icaro on 23/08/16.
 */
class MainFragment : Fragment() {
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

    private lateinit var tvshow_popular_main: RecyclerView
    private lateinit var recycle_tvshowtoday_main: RecyclerView
    private lateinit var recycle_movie_popular_main: RecyclerView
    private lateinit var recycle_movieontheair_main: RecyclerView

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
    }

    private fun setScrollFilmeButton() {
        chip_group_movie.setOnCheckedChangeListener { chipGroup, id ->
            Log.d(this.javaClass.name, "$chipGroup - $id")
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

    private fun getViewMovie(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.filmes_main, container, false)
        recycle_movie_popular_main = view.findViewById<RecyclerView>(R.id.recycle_movie_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        recycle_movieontheair_main = view.findViewById<RecyclerView>(R.id.recycle_movieontheair_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        if (UtilsApp.isNetWorkAvailable(context!!)) {
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    val popular = async(Dispatchers.Default) {
                        Api(context!!).getMoviePopular()
                    }
                    setScrollMoviePopular(popular.await())

                    val upComing = async(Dispatchers.Default) {
                        Api(context!!).getUpcoming()
                    }
                    setScrollMovieOntheAir(upComing.await())
                }
            } catch (ex: java.lang.Exception) {
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun getViewTvshow(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.tvshow_main, container, false)
        tvshow_popular_main = view.findViewById<RecyclerView>(R.id.tvshow_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        recycle_tvshowtoday_main = view.findViewById<RecyclerView>(R.id.recycle_tvshowtoday_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        if (UtilsApp.isNetWorkAvailable(context!!)) {
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    val popular = async(Dispatchers.Default) {
                        Api(context!!).getPopularTv()
                    }
                    setScrollTvShowPopulares(popular.await())
                    val airTv = async(Dispatchers.Default) {
                        Api(context!!).getAiringToday()
                    }
                    setScrollTvShowToDay(airTv.await())
                }
            } catch (ex: java.lang.Exception) {
                Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
        }

        return view
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
}
