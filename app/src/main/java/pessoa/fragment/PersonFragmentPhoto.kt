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
import domain.person.Person
import domain.person.ProfilesItem
import pessoa.adapter.PersonImagemAdapter
import pessoa.modelview.PersonViewModel
import utils.gone
import utils.kotterknife.findView
import utils.patternRecyclerGrid

class PersonFragmentPhoto : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	override val layout: Int = 0 // remover na refatoração. Separar fragmetos
	private val recyclerViewImagem: RecyclerView by findView(R.id.recycleView_person_imagem)
	private val empty: TextView by findView(R.id.empty_pics)
	private val progressBar: ProgressBar by findView(R.id.progress)
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		observers()
	}

	private fun observers() {
		model.response.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					val person = it.result as Person
					setPersonImagem(person.images?.profiles, person.name)
				}
				is Failure -> { ops()
				}
				else -> { }
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		return getViewPersonImage(inflater, container)
	}

	private fun getViewPersonImage(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater!!.inflate(R.layout.activity_person_imagem, container, false)
	}

	private fun setPersonImagem(artworks: List<ProfilesItem?>?, name: String?) {
		recyclerViewImagem.patternRecyclerGrid()
		progressBar.gone()
		if (artworks.isNullOrEmpty()) {
			empty.visibility = View.VISIBLE
		} else {
			recyclerViewImagem.adapter = PersonImagemAdapter(requireActivity(), artworks, name)
		}
	}
}