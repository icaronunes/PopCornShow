package pessoa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import pessoa.adapter.PersonTvAdapter
import pessoa.modelview.PersonViewModel
import utils.gone
import utils.kotterknife.findView
import utils.patternRecyclerGrid
import utils.visible

class PersonFragmentTv : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	private val progressBar: ProgressBar by findView(R.id.progress)
	private val empty: TextView by findView(R.id.empty_tvshows)
	private val adView: AdView by findView(R.id.adView)
	private val recyclerViewTvshow: RecyclerView by findView(R.id.recycleView_person_tvshow)
	override val layout: Int = 0 // remover na refatoração. Separar fragmetos


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		return getViewPersonTvShow(inflater, container)
	}

	private fun getViewPersonTvShow(inflater: LayoutInflater?, container: ViewGroup?): View? {
		return inflater?.inflate(R.layout.activity_person_tvshow, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setAdMob(adView)
		observers()
	}

	private fun observers() {
		model.response.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					val person = it.result as Person
					recyclerViewTvshow.patternRecyclerGrid()
					setPersonCreditsTvshow(person.combinedCredits?.cast?.filter { cast ->
						cast?.mediaType.equals(
							"tv"
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

	private fun setPersonCreditsTvshow(personCredits: List<CastItem?>?) {
		if (personCredits?.isNullOrEmpty() == true) {
			empty.visible()
		} else {
			recyclerViewTvshow.adapter = PersonTvAdapter(requireActivity(), personCredits)
		}
		progressBar.gone()
	}
}

