package pessoa.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import domain.person.Person
import pessoa.modelview.PersonViewModel
import site.Site
import utils.Constant
import utils.gone
import utils.kotterknife.findView
import utils.setPicasso
import utils.visible

class PersonFragmentProfile : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	override val layout: Int = 0 // remover na refatoração. Separar fragmetos
	private var namePerson: TextView? = null
	private val birthDay: TextView by findView(R.id.birthday)
	private val dead: TextView by findView(R.id.dead)
	private val homePage: TextView by findView(R.id.person_homepage)
	private val biografia: TextView by findView(R.id.person_biogragia)
	private val aka: TextView by findView(R.id.aka)
	private val conhecido: TextView by findView(R.id.conhecido)
	private val place_of_birth: TextView by findView(R.id.place_of_birth)
	private val imageView: ImageView by findView(R.id.image_person)
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		observers()
	}

	private fun observers() {
		model.response.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					val person = it.result as Person
					person.fillViews()
				}
				is Failure -> {
				}
				else -> {
				}
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		return getViewPerson(inflater, container)
	}

	private fun getViewPerson(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater?.inflate(R.layout.activity_person_perfil, container, false)!!.apply {
		}
	}

	private fun Person.fillViews() {
		imageView.setPicasso(profilePath, 2, img_erro = R.drawable.person)
		if (!name.isNullOrBlank()) {
			namePerson?.text = name
			namePerson?.visibility = View.VISIBLE
		} else {
			namePerson?.gone()
		}

		if (!birthday.isNullOrBlank()) {
			birthDay.text = birthday
			birthDay.visible()
		} else {
			birthDay.gone()
		}

		if (!deathday.isNullOrBlank()) {
			dead.text = " - ${deathday}"
			dead.visible()
		} else {
			dead.gone()
		}

		if (!homepage.isNullOrBlank()) {
			val site = homepage.replace("http://", "")
			homePage.apply {
				text = site
				contentDescription = "site - $site"
				visible()
				setOnClickListener {
					val intent = Intent(context, Site::class.java)
					intent.putExtra(Constant.SITE, homepage)
					startActivity(intent)
				}
			}
		} else {
			homePage.gone()
		}

		if (!placeOfBirth.isNullOrBlank()) {
			place_of_birth.text = placeOfBirth
			place_of_birth.visible()
		} else {
			place_of_birth.gone()
		}

		if (!biography.isNullOrBlank()) {
			biografia.text = biography
		} else {
			biografia.setText(R.string.sem_biografia)
		}

		if (!alsoKnownAs.isNullOrEmpty()) {
			aka.visible()
			alsoKnownAs.take(3).forEach {
				aka.text = if (aka.text.isBlank()) {
					"$it"
				} else {
					"${aka.text} - $it"
				}
			}
		} else {
			aka.gone()
			conhecido.gone()
		}
	}
}