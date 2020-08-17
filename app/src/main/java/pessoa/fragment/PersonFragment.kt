package pessoa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import domain.person.CastItem
import domain.person.CrewItem
import domain.person.Person
import domain.person.ProfilesItem
import pessoa.adapter.PersonCrewsAdapter
import pessoa.adapter.PersonImagemAdapter
import pessoa.adapter.PersonMovieAdapter
import pessoa.adapter.PersonTvAdapter
import pessoa.modelview.PersonViewModel
import site.Site
import utils.Constant
import utils.gone
import utils.patternRecyclerGrid
import utils.setPicasso
import utils.visible

/**
 * Created by icaro on 18/08/16.
 */
class PersonFragment : BaseFragment() {
	private val model by lazy { createViewModel(PersonViewModel::class.java) }
	override val layout: Int = 0 // remover na refatoração. Separar fragmetos
	private var namePerson: TextView? = null
	private lateinit var birthday: TextView
	private lateinit var dead: TextView
	private lateinit var homepage: TextView
	private lateinit var biografia: TextView
	private lateinit var aka: TextView
	private lateinit var conhecido: TextView
	private lateinit var place_of_birth: TextView
	private lateinit var sem_filmes: TextView
	private lateinit var sem_fotos: TextView
	private lateinit var sem_crews: TextView
	private lateinit var sem_serie: TextView
	private lateinit var imageView: ImageView
	private var recyclerViewMovie: RecyclerView? = null
	private var recyclerViewImagem: RecyclerView? = null
	private var recyclerViewCrews: RecyclerView? = null
	private var recyclerViewTvshow: RecyclerView? = null
	private var tipo: Int = 0
	private lateinit var person: Person
	private var progressBar: ProgressBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (arguments != null) {
			tipo = arguments?.getInt(Constant.ABA)!!
		}
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		observers()
	}

	private fun observers() {
		model.response.observe(this, Observer {
			when (it) {
				is Success -> {
					person = it.result as Person
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
		when (tipo) {
			R.string.filme -> {
				return getViewPersonMovie(inflater, container)
			}
			R.string.producao -> {
				return getViewPersonCrews(inflater, container)
			}
			R.string.person -> {
				return getViewPerson(inflater, container)
			}
			R.string.imagem_person -> {
				return getViewPersonImage(inflater, container)
			}
			R.string.tvshow -> {
				return getViewPersonTvShow(inflater, container)
			}
		}
		return null
	}

	private fun getViewPersonTvShow(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater!!.inflate(R.layout.activity_person_tvshow, container, false).apply {
			sem_serie = findViewById(R.id.sem_tvshow)
			progressBar = findViewById(R.id.progress)
			recyclerViewTvshow =
				findViewById<RecyclerView>(R.id.recycleView_person_tvshow).patternRecyclerGrid()
			setAdMob(findViewById(R.id.adView))

			setPersonCreditsTvshow(person.combinedCredits?.cast?.filter { it ->
				it?.mediaType.equals(
					"tv"
				)
			}
				?.distinctBy { it -> it?.id }
				?.sortedBy { it -> it?.releaseDate }
				?.reversed())
		}
	}

	private fun getViewPersonImage(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater!!.inflate(R.layout.activity_person_imagem, container, false).apply {
			sem_fotos = findViewById(R.id.sem_fotos)
			progressBar = findViewById(R.id.progress)
			recyclerViewImagem =
				findViewById<RecyclerView>(R.id.recycleView_person_imagem).patternRecyclerGrid()
			setPersonImagem(person.images?.profiles)
		}
	}

	private fun getViewPersonCrews(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater!!.inflate(R.layout.activity_person_crews, container, false).apply {
			sem_crews = findViewById(R.id.sem_crews)
			progressBar = findViewById(R.id.progress)
			recyclerViewCrews =
				findViewById<RecyclerView>(R.id.recycleView_person_crews).patternRecyclerGrid()

			setAdMob(findViewById(R.id.adView))

			setPersonCrews(person?.combinedCredits?.crew
				?.distinctBy { it -> it?.id }
				?.sortedBy { it -> it?.releaseDate }
				?.reversed())
		}
	}

	private fun getViewPerson(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater?.inflate(R.layout.activity_person_perfil, container, false)!!.apply {
			namePerson = findViewById(R.id.nome_person)
			birthday = findViewById(R.id.birthday)
			dead = findViewById(R.id.dead)
			homepage = findViewById(R.id.person_homepage)
			biografia = findViewById(R.id.person_biogragia)
			imageView = findViewById(R.id.image_person)
			aka = findViewById(R.id.aka)
			conhecido = findViewById(R.id.conhecido)
			place_of_birth = findViewById(R.id.place_of_birth)
			person.fillViews()
		}
	}

	private fun getViewPersonMovie(inflater: LayoutInflater?, container: ViewGroup?): View {
		return inflater!!.inflate(R.layout.activity_person_movies, container, false).apply {
			sem_filmes = findViewById(R.id.sem_filmes)
			progressBar = findViewById(R.id.progress)
			recyclerViewMovie =
				findViewById<RecyclerView>(R.id.recycleView_person_movies).patternRecyclerGrid()
			setPersonMovies(person.combinedCredits?.cast?.filter { it -> it?.mediaType.equals("movie") }
				?.distinctBy { it -> it?.id }
				?.sortedBy { it -> it?.releaseDate }
				?.reversed())

			setAdMob(findViewById(R.id.adView))
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
			this@PersonFragment.birthday.text = birthday
			this@PersonFragment.birthday.visible()
		} else {
			this@PersonFragment.birthday.gone()
		}

		if (!deathday.isNullOrBlank()) {
			dead.text = " - ${deathday}"
			dead.visible()
		} else {
			dead.gone()
		}

		if (!homepage.isNullOrBlank()) {
			val site = homepage.replace("http://", "")
			this@PersonFragment.homepage.apply {
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
			this@PersonFragment.homepage.gone()
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

	private fun setPersonMovies(personMovies: List<CastItem?>?) {
		progressBar?.gone()
		if (personMovies.isNullOrEmpty()) {
			sem_filmes.visible()
		} else {
			recyclerViewMovie?.adapter = PersonMovieAdapter(requireActivity(), personMovies)
		}
	}

	private fun setPersonCrews(personCredits: List<CrewItem?>?) {
		progressBar?.gone()
		if (personCredits.isNullOrEmpty()) {
			sem_crews.visible()
		} else {
			recyclerViewCrews?.adapter = PersonCrewsAdapter(requireActivity(), personCredits)
		}
	}

	private fun setPersonImagem(artworks: List<ProfilesItem?>?) {
		progressBar?.gone()
		if (artworks.isNullOrEmpty()) {
			sem_fotos.visibility = View.VISIBLE
		} else {
			recyclerViewImagem?.adapter =
				PersonImagemAdapter(requireActivity(), artworks, person.name)
		}
	}

	private fun setPersonCreditsTvshow(personCredits: List<CastItem?>?) {
		if (personCredits?.isEmpty()!!) {
			sem_serie.visible()
		} else {
			recyclerViewTvshow?.adapter = PersonTvAdapter(requireActivity(), personCredits)
		}
		progressBar?.gone()
	}

	companion object {
		fun newInstance(aba: Int): PersonFragment {
			return PersonFragment().apply {
				arguments = Bundle().apply { putInt(Constant.ABA, aba) }
			}
		}
	}
}

