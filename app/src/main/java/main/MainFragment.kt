package main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.filmes_main
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.android.synthetic.main.filmes_main.chip_group_movie
import kotlinx.android.synthetic.main.filmes_main.recycle_movie_popular_main
import kotlinx.android.synthetic.main.filmes_main.recycle_upcoming_movie_main
import kotlinx.android.synthetic.main.tvshow_main.chip_group_tvshow
import kotlinx.android.synthetic.main.tvshow_main.recycle_tvshowtoday_main
import kotlinx.android.synthetic.main.tvshow_main.tvshow_popular_main
import listafilmes.activity.MoviesActivity
import listaserie.activity.TvShowsActivity
import tvshow.TvShowMainAdapter
import utils.Api.TYPESEARCH.FILME
import utils.Api.TYPESEARCH.SERIE
import utils.Constant

/**
 * Created by icaro on 23/08/16.
 */
class MainFragment : BaseFragment() {

    private var tipo: Int = 0
    private lateinit var model: MainFragViewModel

    companion object {
        fun newInstance(info: Int): Fragment {
            return MainFragment().apply {
                arguments = Bundle().apply { putInt(Constant.ABA, info) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments!!.getInt(Constant.ABA)
        }
        model = createViewModel(MainFragViewModel::class.java) as MainFragViewModel
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
            when (it) {
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
                    startActivity(Intent(activity, MoviesActivity::class.java).apply {
                        putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.agora)
                    })
                }

                R.id.chip_upcoming -> {
                    startActivity(Intent(activity, MoviesActivity::class.java).apply {
                        putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.chegando)
                    })
                }

                R.id.chip_populares -> {
                    startActivity(Intent(activity, MoviesActivity::class.java).apply {
                        putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.popular)
                    })
                }

                R.id.chip_top_rated -> {
                    startActivity(Intent(activity, MoviesActivity::class.java).apply {
                        putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.melhores)
                    })
                }
            }
        }
    }

    private fun setScrollTvshowButton() {
        chip_group_tvshow.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.chip_air_data -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.semana)
                    startActivity(intent)
                }
                R.id.chip_today -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.hoje)
                    startActivity(intent)
                }
                R.id.chip_populares_tvshow -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.popular)
                    startActivity(intent)
                }

                R.id.chip_top_rated_tvshow -> {
                    val intent = Intent(activity, TvShowsActivity::class.java)
                    intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.melhores)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setScrollTvShowToDay(toDay: ListaSeries) {
        recycle_tvshowtoday_main.adapter = TvShowMainAdapter(requireActivity(), toDay)
    }

    private fun setScrollTvShowPopulares(popularTvShow: ListaSeries) {
        tvshow_popular_main.adapter = TvShowMainAdapter(requireActivity(), popularTvShow)
    }

    private fun setScrollMoviePopular(popular: ListaFilmes) {
        recycle_movie_popular_main.adapter = MovieMainAdapter(requireActivity(), popular)
    }

    private fun setScrollUpComing(airDay: ListaFilmes) {
        recycle_upcoming_movie_main.adapter = MovieMainAdapter(requireActivity(), airDay)
    }
}
