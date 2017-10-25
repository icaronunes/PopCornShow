package pessoa.fragment

import activity.Site
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.person.CastItem
import domain.person.CrewItem
import domain.person.Person
import domain.person.ProfilesItem
import pessoa.adapter.PersonCrewsAdapter
import pessoa.adapter.PersonImagemAdapter
import pessoa.adapter.PersonMovieAdapter
import pessoa.adapter.PersonTvAdapter
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 18/08/16.
 */
class PersonFragment : Fragment() {

    private var nome_person: TextView? = null
    private var birthday: TextView? = null
    private var dead: TextView? = null
    private var homepage: TextView? = null
    private var biografia: TextView? = null
    private var aka: TextView? = null
    private var conhecido: TextView? = null
    private var place_of_birth: TextView? = null
    private var sem_filmes: TextView? = null
    private var sem_fotos: TextView? = null
    private var sem_crews: TextView? = null
    private var sem_serie: TextView? = null
    private var imageView: ImageView? = null
    private var imageButtonWiki: ImageView? = null
    private var recyclerViewMovie: RecyclerView? = null
    private var recyclerViewImagem: RecyclerView? = null
    private var recyclerViewCrews: RecyclerView? = null
    private var recyclerViewTvshow: RecyclerView? = null
    private var tipo: Int = 0
    private var person: Person? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        if (arguments != null) {
            tipo = arguments.getInt(Constantes.ABA)
            if (person == null) {
                person = arguments.getSerializable(Constantes.PERSON) as Person
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        val view = inflater?.inflate(R.layout.activity_person_tvshow, container, false)
        recyclerViewTvshow = view?.findViewById<View>(R.id.recycleView_person_tvshow) as RecyclerView
        sem_serie = view.findViewById<View>(R.id.sem_tvshow) as TextView
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        recyclerViewTvshow?.layoutManager = GridLayoutManager(context, 2)
        recyclerViewTvshow?.setHasFixedSize(true)
        recyclerViewTvshow?.itemAnimator = DefaultItemAnimator()

        val adview = view.findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adview.loadAd(adRequest)

        setPersonCreditsTvshow(person?.combinedCredits?.cast?.filter { it -> it?.mediaType.equals("tv") }
                ?.distinctBy { it -> it?.id }
                ?.sortedBy { it -> it?.releaseDate }
                ?.reversed() )

        return view
    }

    private fun getViewPersonImage(inflater: LayoutInflater?, container: ViewGroup?): View {

        val view = inflater?.inflate(R.layout.activity_person_imagem, container, false)
        recyclerViewImagem = view?.findViewById<View>(R.id.recycleView_person_imagem) as RecyclerView
        sem_fotos = view.findViewById<View>(R.id.sem_fotos) as TextView
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        recyclerViewImagem?.layoutManager = GridLayoutManager(context, 2)
        recyclerViewImagem?.setHasFixedSize(true)
        recyclerViewImagem?.itemAnimator = DefaultItemAnimator()
        setPersonImagem(person?.images?.profiles)

        return view
    }

    private fun getViewPersonCrews(inflater: LayoutInflater?, container: ViewGroup?): View {
        val view = inflater?.inflate(R.layout.activity_person_crews, container, false)
        recyclerViewCrews = view?.findViewById<View>(R.id.recycleView_person_crews) as RecyclerView
        sem_crews = view?.findViewById<View>(R.id.sem_crews) as TextView
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        recyclerViewCrews?.itemAnimator = DefaultItemAnimator()
        recyclerViewCrews?.layoutManager = GridLayoutManager(context, 2)
        recyclerViewCrews?.setHasFixedSize(true)

        val adview = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adview.loadAd(adRequest)

        setPersonCrews(person?.combinedCredits?.crew
                ?.distinctBy { it -> it?.id }
                ?.sortedBy { it -> it?.releaseDate }
                ?.reversed())

        return view
    }

    private fun getViewPerson(inflater: LayoutInflater?, container: ViewGroup?): View {

        val view = inflater?.inflate(R.layout.activity_person_perfil, container, false)
        nome_person = view?.findViewById<View>(R.id.nome_person) as TextView
        birthday = view.findViewById<View>(R.id.birthday) as TextView
        dead = view.findViewById<View>(R.id.dead) as TextView
        homepage = view.findViewById<View>(R.id.person_homepage) as TextView
        biografia = view.findViewById<View>(R.id.person_biogragia) as TextView
        imageView = view.findViewById<View>(R.id.image_person) as ImageView
        aka = view.findViewById<View>(R.id.aka) as TextView
        imageButtonWiki = view.findViewById<View>(R.id.person_wiki) as ImageView
        conhecido = view.findViewById<View>(R.id.conhecido) as TextView
        place_of_birth = view.findViewById<View>(R.id.place_of_birth) as TextView
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        setPersonInformation(person)
        return view
    }

    private fun getViewPersonMovie(inflater: LayoutInflater?, container: ViewGroup?): View {

        val view = inflater?.inflate(R.layout.activity_person_movies, container, false)
        recyclerViewMovie = view?.findViewById<View>(R.id.recycleView_person_movies) as RecyclerView
        sem_filmes = view.findViewById<View>(R.id.sem_filmes) as TextView
        progressBar = view.findViewById<View>(R.id.progress) as ProgressBar
        recyclerViewMovie?.layoutManager = GridLayoutManager(context, 2)
        recyclerViewMovie?.setHasFixedSize(true)

        setPersonMovies(person?.combinedCredits?.cast?.filter { it -> it?.mediaType.equals("movie") }
                ?.distinctBy { it -> it?.id }
                ?.sortedBy { it -> it?.releaseDate }
                ?.reversed())

        val adview = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adview.loadAd(adRequest)

        return view
    }

    private fun setPersonInformation(information: Person?) {
        if (information == null) {
            return
        }

        if (information.name != null) {
            nome_person?.text = information.name
            nome_person?.visibility = View.VISIBLE
        }
        if (information.birthday != null) {
            birthday?.text = information.birthday
            birthday?.visibility = View.VISIBLE
        }

        if (information.deathday != null) {
            dead?.text = " - " + information.deathday
            dead?.visibility = View.VISIBLE
        }

        if (information.homepage != null) {
            var site = information.homepage
            site = site.replace("http://", "")

            homepage?.text = site
            homepage?.visibility = View.VISIBLE

            homepage?.setOnClickListener { view ->
                val intent = Intent(context, Site::class.java)
                intent.putExtra(Constantes.SITE, information.homepage)
                startActivity(intent)

            }

        } else {
            homepage?.visibility = View.GONE
        }

        if (information.placeOfBirth != null) {
            place_of_birth?.text = information.placeOfBirth
            place_of_birth?.visibility = View.VISIBLE
        }

        if (information.biography != null) {
            biografia?.text = information.biography
        } else {
            biografia?.setText(R.string.sem_biografia)
        }

        if (information.name != null) {
            imageButtonWiki?.visibility = View.VISIBLE

            imageButtonWiki?.setOnClickListener { view ->
                val BASEWIKI = "https://pt.wikipedia.org/wiki/"
                val site: String
                val intent = Intent(context, Site::class.java)
                val nome = information.name
                site = BASEWIKI + nome.replace(" ", "_")

                intent.putExtra(Constantes.SITE, site)
                startActivity(intent)

            }
        }


        Picasso.with(context).load(UtilsApp.getBaseUrlImagem(2) + information.profilePath)
                .placeholder(R.drawable.person)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView?.visibility = View.VISIBLE
                        progressBar?.visibility = View.GONE
                    }

                    override fun onError() {
                        progressBar?.visibility = View.VISIBLE
                    }
                })

    }

    private fun setPersonMovies(personMovies: List<CastItem?>?) {

        if (personMovies?.isEmpty()!!) {
            sem_filmes?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        } else {
            progressBar?.visibility = View.GONE
            recyclerViewMovie?.adapter = PersonMovieAdapter(context, personMovies)
            progressBar?.visibility = View.GONE
        }
    }

    private fun setPersonCrews(personCredits: List<CrewItem?>?) {

        if (personCredits?.isEmpty()!!) {
            sem_crews?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            return
        }
        recyclerViewCrews?.adapter = PersonCrewsAdapter(context, personCredits)
        progressBar?.visibility = View.GONE


    }

    private fun setPersonImagem(artworks: List<ProfilesItem?>?) {
        if (artworks == null) {
            return
        }

        if (artworks.isEmpty()) {
            sem_fotos?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        } else {
            recyclerViewImagem?.adapter = PersonImagemAdapter(context, artworks, person?.name)
            progressBar?.visibility = View.GONE
        }
    }

    private fun setPersonCreditsTvshow(personCredits: List<CastItem?>?) {

        if (personCredits?.isEmpty()!!) {
            sem_serie?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            return
        }

        recyclerViewTvshow?.adapter = PersonTvAdapter(context, personCredits)
        progressBar?.visibility = View.GONE

    }

    companion object {

        fun newInstance(aba: Int, person: Person): PersonFragment {

            val args = Bundle()
            args.putInt(Constantes.ABA, aba)
            args.putSerializable(Constantes.PERSON, person)
            val fragment = PersonFragment()
            fragment.arguments = args
            return fragment
        }
    }
}


