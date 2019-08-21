package main

import BaseFragment
import adapter.MovieMainAdapter
import adapter.TvShowMainAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.filmes_main
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.android.synthetic.main.filmes_main.*
import kotlinx.android.synthetic.main.tvshow_main.*
import kotlinx.coroutines.Job
import listafilmes.activity.FilmesActivity
import listaserie.activity.TvShowsActivity
import utils.Constantes


/**
 * Created by icaro on 23/08/16.
 */
class MainFragment : BaseFragment() {


    private var rotina: Job = Job()
    private var tipo: Int = 0
    private lateinit var model: MainFragViewModel

    companion object {
        fun newInstance(info: Int): Fragment {
            return MainFragment().apply {
                arguments = Bundle().apply { putInt(Constantes.ABA, info) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments!!.getInt(Constantes.ABA)
        }
        model = createViewModel(MainFragViewModel::class.java)
        setObservers()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (tipo == R.string.tvshow_main) {
            setScrollTvshowButton()
        } else {
            setScrollFilmeButton()
        }
    }

    private fun setTvShowList() {
        model.setAiringToday()
        model.getPopularTv()
    }

    private fun setMoviesList() {
        model.setMoviesUpComing()
        model.setMoviesPopular()
    }

    private fun setObservers() {
        model.data.observe(this, Observer {
            when(it) {
                is MainFragViewModel.MainFragModel.ModelUpComing -> setScrollUpComing(it.movies)
                is MainFragViewModel.MainFragModel.ModelPopularMovie -> setScrollMoviePopular(it.movies)
                is MainFragViewModel.MainFragModel.ModelPopularTvshow -> setScrollTvShowPopulares(it.tvshows)
                is MainFragViewModel.MainFragModel.ModelAiringToday -> setScrollTvShowToDay(it.tvshows)
            }
        })
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
        inflaterRecyclerMovie(view)
        setMoviesList()
        return view
    }

    private fun inflaterRecyclerMovie(view: View) {
        inflaterMoviePopular(view)
        inflaterUpcomingMovie(view)
    }

    private fun inflaterUpcomingMovie(view: View) {
        view.findViewById<RecyclerView>(R.id.recycle_upcoming_movie_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun inflaterMoviePopular(view: View) {
        view.findViewById<RecyclerView>(R.id.recycle_movie_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun inflaterRecyclerTv(view: View) {
        inflaterTvPopular(view)
        inflaterTvToday(view)
    }

    private fun inflaterTvPopular(view: View) {
        view.findViewById<RecyclerView>(R.id.tvshow_popular_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun inflaterTvToday(view: View) {
        view.findViewById<RecyclerView>(R.id.recycle_tvshowtoday_main).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun getViewTvshow(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.tvshow_main, container, false)
        inflaterRecyclerTv(view)
        setTvShowList()
        return view
    }

    private fun setScrollFilmeButton() {
        chip_group_movie.setOnCheckedChangeListener { _, id ->
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

    private fun setScrollTvShowToDay(toDay: ListaSeries) {
        recycle_tvshowtoday_main.adapter = TvShowMainAdapter(activity, toDay)
    }

    private fun setScrollTvShowPopulares(popularTvShow: ListaSeries) {
        tvshow_popular_main.adapter = TvShowMainAdapter(activity, popularTvShow)
    }

    private fun setScrollMoviePopular(popular: ListaFilmes) {
        recycle_movie_popular_main.adapter = MovieMainAdapter(activity, popular)
    }

    private fun setScrollUpComing(airDay: ListaFilmes) {
        recycle_upcoming_movie_main.adapter = MovieMainAdapter(activity, airDay)
    }

    override fun onDestroy() {
        if (rotina.isActive) rotina.cancel()
        super.onDestroy()
    }
}
