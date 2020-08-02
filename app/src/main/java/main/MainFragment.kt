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
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.android.synthetic.main.filmes_main.chip_group_movie
import kotlinx.android.synthetic.main.tvshow_main.chip_group_tvshow
import listafilmes.activity.MoviesActivity
import listaserie.activity.TvShowsActivity
import tvshow.TvShowMainAdapter
import utils.Api.TYPESEARCH.FILME
import utils.Api.TYPESEARCH.SERIE
import utils.Constant
import utils.kotterknife.bindArgument

/**
 * Created by icaro on 23/08/16.
 */
class MainFragment : BaseFragment() {

	private lateinit var recyclerTodayTv: RecyclerView
	private lateinit var recyclerPopularTv: RecyclerView
	private lateinit var recyclerUpcomingmovie: RecyclerView
	private lateinit var recyclerNowPlaingMovie: RecyclerView
	private val tipo: Int by bindArgument(Constant.ABA)
	private val model: MainViewModel by lazy { createViewModel(MainViewModel::class.java) }

	companion object {
		fun newInstance(info: Int): Fragment {
			return MainFragment().apply {
				arguments = Bundle().apply { putInt(Constant.ABA, info) }
			}
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		if (tipo == R.string.tvshow_main) {
			setScrollTvshowButton()
			observersTv()
		} else {
			setScrollFilmeButton()
			observersMovie()
		}
	}

	private fun observersMovie() {
		model.upComingMovie.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> setScrollUpComing(it.result)
				is Failure -> {
				}
				is Loading -> {
				}
			}
		})

		model.nowPlayingMovie.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> setScrollMovieNowPaying(it.result)
				is Failure -> {
				}
				is Loading -> {
				}
			}
		})
	}

	private fun observersTv() {
		model.airingTodayTv.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> setScrollTvShowToDay(it.result)
				is Failure -> {
				}
				is Loading -> {
				}
			}
		})

		model.popularTv.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> setScrollTvShowPopulares(it.result)
				is Failure -> {
				}
				is Loading -> {
				}
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return when (tipo) {
			R.string.filmes_main -> getViewMovie(inflater, container)
			R.string.tvshow_main -> getViewTvshow(inflater, container)
			else -> null
		}
	}


	private fun getViewMovie(inflater: LayoutInflater, container: ViewGroup?): View {
		val view = inflater.inflate(R.layout.filmes_main, container, false)
		inflaterRecyclerMovie(view)
		return view
	}

	private fun inflaterRecyclerMovie(view: View) {
		inflaterMoviePopular(view)
		inflaterUpcomingMovie(view)
	}

	private fun inflaterUpcomingMovie(view: View) {
		recyclerUpcomingmovie =
			view.findViewById<RecyclerView>(R.id.recycle_upcoming_movie_main).apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
			}
	}

	private fun inflaterMoviePopular(view: View) {
		recyclerNowPlaingMovie =
			view.findViewById<RecyclerView>(R.id.recycle_movie_now_playing_main).apply {
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
		recyclerPopularTv = view.findViewById<RecyclerView>(R.id.tvshow_popular_main).apply {
			setHasFixedSize(true)
			itemAnimator = DefaultItemAnimator()
			layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
		}
	}

	private fun inflaterTvToday(view: View) {
		recyclerTodayTv = view.findViewById<RecyclerView>(R.id.recycle_tvshowtoday_main).apply {
			setHasFixedSize(true)
			itemAnimator = DefaultItemAnimator()
			layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
		}
	}

	private fun getViewTvshow(inflater: LayoutInflater, container: ViewGroup?): View {
		val view = inflater.inflate(R.layout.tvshow_main, container, false)
		inflaterRecyclerTv(view)
		return view
	}

	private fun setScrollFilmeButton() {
		chip_group_movie.setOnCheckedChangeListener { _, id ->
			when (id) {
				R.id.chip_now_playing -> {
					startActivity(Intent(activity, MoviesActivity::class.java).apply {
						putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.now)
					})
				}

				R.id.chip_upcoming -> {
					startActivity(Intent(activity, MoviesActivity::class.java).apply {
						putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.upComing)
					})
				}

				R.id.chip_populares -> {
					startActivity(Intent(activity, MoviesActivity::class.java).apply {
						putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.popular)
					})
				}

				R.id.chip_top_rated -> {
					startActivity(Intent(activity, MoviesActivity::class.java).apply {
						putExtra(Constant.NAV_DRAW_ESCOLIDO, FILME.bestScore)
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
					intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.week)
					startActivity(intent)
				}
				R.id.chip_today -> {
					val intent = Intent(activity, TvShowsActivity::class.java)
					intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.toDay)
					startActivity(intent)
				}
				R.id.chip_populares_tvshow -> {
					val intent = Intent(activity, TvShowsActivity::class.java)
					intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.popular)
					startActivity(intent)
				}

				R.id.chip_top_rated_tvshow -> {
					val intent = Intent(activity, TvShowsActivity::class.java)
					intent.putExtra(Constant.NAV_DRAW_ESCOLIDO, SERIE.bestScore)
					startActivity(intent)
				}
			}
		}
	}

	private fun setScrollTvShowToDay(toDay: ListaSeries) {
		recyclerTodayTv.adapter = TvShowMainAdapter(requireActivity(), toDay)
	}

	private fun setScrollTvShowPopulares(popularTvShow: ListaSeries) {
		recyclerPopularTv.adapter = TvShowMainAdapter(requireActivity(), popularTvShow)
	}

	private fun setScrollMovieNowPaying(popular: ListaFilmes) {
		recyclerNowPlaingMovie.adapter = MovieMainAdapter(requireActivity(), popular)
	}

	private fun setScrollUpComing(airDay: ListaFilmes) {
		recyclerUpcomingmovie.adapter = MovieMainAdapter(requireActivity(), airDay)
	}
}
