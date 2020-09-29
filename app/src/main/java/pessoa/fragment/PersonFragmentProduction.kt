package pessoa.fragment

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import domain.person.CrewItem
import domain.person.Person
import pessoa.adapter.PersonCrewsAdapter
import pessoa.modelview.PersonViewModel
import utils.gone
import utils.kotterknife.findView
import utils.patternRecyclerGrid
import utils.visible

class PersonFragmentProduction(override val layout: Int = R.layout.activity_person_crews) : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	private val recyclerViewCrews: RecyclerView by findView(R.id.recycleView_person_crews)
	private val adView: AdView by findView(R.id.adView)
	private val progressBar: ProgressBar by findView(R.id.progress)
	private val empty: TextView by findView(R.id.empty)
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		observers()
	}

	private fun observers() {
		model.response.observe(viewLifecycleOwner, Observer {
			setAdMob(adView)
			when (it) {
				is Success -> {
					val person = it.result as Person
					recyclerViewCrews.patternRecyclerGrid()
					setPersonCrews(person.combinedCredits?.crew
						?.distinctBy { it -> it?.id }
						?.sortedBy { it -> it?.releaseDate }
						?.reversed())
					setPersonCrews(person.combinedCredits?.crew)
				}
				is Failure -> {
				}
				else -> {
				}
			}
		})
	}

	private fun setPersonCrews(personCredits: List<CrewItem?>?) {
		progressBar.gone()
		if (personCredits.isNullOrEmpty()) {
			empty.visible()
		} else {
			recyclerViewCrews.adapter = PersonCrewsAdapter(requireActivity(), personCredits)
		}
	}
}