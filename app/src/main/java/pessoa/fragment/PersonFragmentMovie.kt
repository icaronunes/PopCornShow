package pessoa.fragment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import domain.person.CastItem
import domain.person.Person
import pessoa.adapter.PersonMovieAdapter
import pessoa.modelview.PersonViewModel
import utils.gone
import utils.kotterknife.findView
import utils.patternRecyclerGrid
import utils.visible

class PersonFragmentMovie(override val layout: Int = R.layout.activity_person_movies) : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	private val recyclerViewMovie: RecyclerView by findView(R.id.recycleView_person_movies)
	private val progressBar: ProgressBar by findView(R.id.progress)
	private val empty: TextView by findView(R.id.sem_filmes)
	private val adView: AdView by findView(R.id.adview)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAdMob(adView)
		observers()
	}

	private fun observers() {
		model.response.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					val person = it.result as Person
					recyclerViewMovie.patternRecyclerGrid()
					setPersonMovies(person.combinedCredits?.cast?.filter { cast ->
						cast?.mediaType.equals(
							"movie"
						)
					}?.distinctBy { it -> it?.id }?.sortedBy { it -> it?.releaseDate }?.reversed())
				}
				is Failure -> {
				}
				else -> {
				}
			}
		})
	}

	private fun setPersonMovies(personMovies: List<CastItem?>?) {
		if (personMovies.isNullOrEmpty()) {
			empty.visible()
		} else {
			recyclerViewMovie.adapter = PersonMovieAdapter(requireActivity(), personMovies)
		}
		progressBar.gone()
	}
}