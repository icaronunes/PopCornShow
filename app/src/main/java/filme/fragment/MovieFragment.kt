package filme.fragment

import adapter.CastAdapter
import adapter.CrewAdapter
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import utils.Api
import domain.Imdb
import domain.Movie
import domain.colecao.PartsItem
import elenco.ElencoActivity
import filme.adapter.CollectionPagerAdapter
import filme.adapter.SimilaresFilmesAdapter
import fragment.FragmentBase
import kotlinx.android.synthetic.main.info_details_movie_layout.icon_collection
import kotlinx.android.synthetic.main.info_details_movie_layout.icon_site
import kotlinx.android.synthetic.main.info_details_movie_layout.img_budget
import kotlinx.android.synthetic.main.info_details_movie_layout.img_star
import kotlinx.android.synthetic.main.info_details_movie_layout.original_title
import kotlinx.android.synthetic.main.info_details_movie_layout.popularity
import kotlinx.android.synthetic.main.info_details_movie_layout.production_countries
import kotlinx.android.synthetic.main.info_details_movie_layout.spoken_languages
import kotlinx.android.synthetic.main.info_details_movie_layout.status
import kotlinx.android.synthetic.main.info_details_movie_layout.voto_media
import kotlinx.android.synthetic.main.movie_details_info.adView
import kotlinx.android.synthetic.main.movie_details_info.categoria_filme
import kotlinx.android.synthetic.main.movie_details_info.descricao
import kotlinx.android.synthetic.main.movie_details_info.imdb_site
import kotlinx.android.synthetic.main.movie_details_info.lancamento
import kotlinx.android.synthetic.main.movie_details_info.produtora
import kotlinx.android.synthetic.main.movie_details_info.recycle_filme_elenco
import kotlinx.android.synthetic.main.movie_details_info.recycle_filme_producao
import kotlinx.android.synthetic.main.movie_details_info.recycle_filme_similares
import kotlinx.android.synthetic.main.movie_details_info.recycle_filme_trailer
import kotlinx.android.synthetic.main.movie_details_info.textview_crews
import kotlinx.android.synthetic.main.movie_details_info.textview_elenco
import kotlinx.android.synthetic.main.movie_details_info.textview_similares
import kotlinx.android.synthetic.main.movie_details_info.time_filme
import kotlinx.android.synthetic.main.movie_details_info.titulo_text
import kotlinx.android.synthetic.main.movie_details_info.tmdb_site
import kotlinx.android.synthetic.main.poster_movie_details_layout.card_poster
import kotlinx.android.synthetic.main.poster_movie_details_layout.img_poster
import poster.PosterGridActivity
import producao.CrewsActivity
import produtora.activity.ProdutoraActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import similares.SimilaresActivity
import site.Site
import utils.Constantes
import utils.Constantes.BASEMOVIEDB_MOVIE
import utils.Constantes.IMDB
import utils.Constantes.METACRITICMOVIE
import utils.Constantes.ROTTENTOMATOESMOVIE
import utils.gone
import utils.makeToast
import utils.parseDate
import utils.putString
import utils.removerAcentos
import utils.setPicasso
import utils.setScrollInvisibleFloatMenu
import utils.visible
import java.io.Serializable
import java.text.DecimalFormat
import java.util.Locale

/**
 * Created by icaro on 03/07/16.
 */

class MovieFragment : FragmentBase() {

    private lateinit var movieDb: Movie
    private var imdbDd: Imdb? = null
    private var color: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val bundle = arguments
            movieDb = bundle?.getSerializable(Constantes.FILME) as Movie
            color = bundle.getInt(Constantes.COLOR_TOP, 0)
        }
        subscriptions = CompositeSubscription()
    }

    override fun onStart() {
        super.onStart()
        getImdb()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_details_info, container, false).apply {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setTitulo()
        setCategoria()
        setLancamento()
        setTimeFilme()
        setProdutora()
        setSinopse()
        setPoster()
        setBuget()
        setHome()
        setOriginalTitle()
        setSpokenLanguages()
        setProductionCountries()
        setPopularity()
        setCollectoin()
        setCast()
        setCrews()
        setTrailer()
        setSimilares()
        setAnimated()
        setStatus()
        setAdMob(adView)

        imdb_site.setOnClickListener {
            startActivity(Intent(requireActivity(), Site::class.java).apply {
                putExtra(Constantes.SITE, "${IMDB}${movieDb.imdbId}/")
            })
        }

        tmdb_site?.setOnClickListener {
            startActivity(Intent(requireActivity(), Site::class.java).apply {
                putExtra(Constantes.SITE, "${BASEMOVIEDB_MOVIE}${movieDb.id}/")
            })
        }

        img_budget?.setOnClickListener {
            movieDb.budget?.let {
                if (it > 0) {
                    var valor = it.toString()
                    if (valor.length >= 6)
                        valor = valor.substring(0, valor.length - 6)
                    snack(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu), "${getString(R.string.orcamento_budget)} $$valor ${getString(R.string.milhoes_budget)}")
                } else {
                    snack(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu), R.string.no_budget)
                }

            }
        }

        icon_site?.setOnClickListener {
            movieDb.homepage?.let {
                if (it.isNotBlank()) {
                    startActivity(Intent(requireActivity(), Site::class.java).apply {
                        putExtra(Constantes.SITE, it)
                    })
                } else {
                    snack(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu), R.string.no_site)
                }
            }
        }

        img_star.setOnClickListener(onClickImageStar())

        icon_collection.setOnClickListener {
            if (movieDb.belongsToCollection != null) {
                subscriptions.add(Api(context = requireActivity()).getColecao(movieDb.belongsToCollection?.id!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        getCollection(it?.parts?.sortedBy { it?.releaseDate })
                    }, { _ ->
                        requireActivity().makeToast(R.string.ops)
                    }))
                it.contentDescription = R.string.sem_informacao_colletion.putString(requireContext())
            } else {
                snack(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu), R.string.sem_informacao_colletion)
            }
        }

        textview_elenco.setOnClickListener {
            val intent = Intent(requireActivity(), ElencoActivity::class.java)
            intent.putExtra(Constantes.ELENCO, movieDb.credits?.cast as Serializable)
            intent.putExtra(Constantes.NOME, movieDb.title)
            startActivity(intent)
        }

        textview_crews.setOnClickListener {
            val intent = Intent(requireActivity(), CrewsActivity::class.java)
            intent.putExtra(Constantes.PRODUCAO, movieDb.credits?.crew as Serializable)
            intent.putExtra(Constantes.NOME, movieDb.title)
            startActivity(intent)
        }

        textview_similares.setOnClickListener {
            val intent = Intent(requireActivity(), SimilaresActivity::class.java)
            intent.putExtra(Constantes.SIMILARES_FILME, movieDb.similar?.resultsSimilar as Serializable)
            intent.putExtra(Constantes.NOME, movieDb.title)
            startActivity(intent)
        }
    }

    private fun setStatus() {
        movieDb.status.let {
            status.text = it
            status.setTextColor(color)
        }
    }

    @SuppressLint("InflateParams")
    private fun onClickImageStar(): View.OnClickListener? {
        return View.OnClickListener {
            if (mediaNotas > 0) {
                val builder = AlertDialog.Builder(requireActivity())
                val inflater = requireActivity().layoutInflater
                val layout = inflater.inflate(R.layout.layout_notas, null)

                imdbDd?.imdbRating?.let {
                    layout.findViewById<TextView>(R.id.nota_imdb)
                        .text = String.format(getString(R.string.bar_ten), it)
                }

                imdbDd?.tomatoRating?.let {
                    layout.findViewById<TextView>(R.id.nota_tomatoes)
                        .text = String.format(getString(R.string.bar_ten), it)
                }

                imdbDd?.imdbRating?.let {
                    layout.findViewById<TextView>(R.id.nota_imdb)
                        .text = String.format(getString(R.string.bar_ten), it)
                }

                imdbDd?.metascore?.let {
                    layout.findViewById<TextView>(R.id.nota_metacritic)
                        .text = String.format(getString(R.string.bar_hundred), it)
                }

                movieDb.voteAverage?.let {
                    layout.findViewById<TextView>(R.id.nota_tmdb)
                        .text = String.format(getString(R.string.bar_ten), it)
                }

                layout.findViewById<ImageView>(R.id.image_metacritic)
                    .setOnClickListener {
                        imdbDd?.let {
                            imdbDd?.title?.let {
                                val clenaName = it.replace(" ", "-").toLowerCase(Locale.ROOT).removerAcentos()
                                val url = "$METACRITICMOVIE$clenaName"
                                startActivity(Intent(requireActivity(), Site::class.java).apply {
                                    putExtra(Constantes.SITE, url)
                                })
                            }
                        }
                    }

                layout.findViewById<ImageView>(R.id.image_tomatoes)
                    .setOnClickListener {
                        imdbDd?.let {
                            imdbDd?.title?.let {
                                val cleanName = it.replace(" ", "_").toLowerCase(Locale.ROOT).removerAcentos()
                                startActivity(Intent(requireActivity(), Site::class.java).apply {
                                    putExtra(Constantes.SITE, "$ROTTENTOMATOESMOVIE$cleanName")
                                })
                            }
                        }
                    }

                layout.findViewById<ImageView>(R.id.image_imdb)
                    .setOnClickListener OnClickListener@{
                        imdbDd?.let {
                            startActivity(Intent(requireActivity(), Site::class.java).apply {
                                putExtra(Constantes.SITE, "$IMDB${imdbDd?.imdbID}")
                            })
                        }
                    }

                layout.findViewById<ImageView>(R.id.image_tmdb)
                    .setOnClickListener {
                        val url = "$BASEMOVIEDB_MOVIE${movieDb.id}"
                        startActivity(Intent(requireActivity(), Site::class.java).apply {
                            putExtra(Constantes.SITE, url)
                        })
                    }

                builder.setView(layout)
                builder.show()
            } else {
                snack(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu), getString(R.string.no_vote))
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun getCollection(colecao: List<PartsItem?>?) {
        if (colecao?.isNotEmpty()!!) {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val dialogCollection = inflater.inflate(R.layout.dialog_collection, null)
            val pager = dialogCollection?.findViewById<ViewPager>(R.id.viewpager_collection)
            pager?.adapter = CollectionPagerAdapter(colecao, requireActivity())
            builder.setView(dialogCollection)
            builder.show()
        } else {
            requireActivity().makeToast(R.string.sem_informacao_colletion)
        }
    }

    private fun setSinopse() {
        if (movieDb.overview.isNullOrBlank()) {
            descricao.text = getString(R.string.sem_sinopse)
        } else {
            descricao.text = movieDb.overview
        }
    }

    private fun setBuget() {
        movieDb.budget?.let {
            if (it > 0) img_budget.setImageResource(R.drawable.orcamento) else img_budget.setImageResource(R.drawable.sem_orcamento)
        }
    }

    private fun setAnimated() {
        AnimatorSet().apply {
            playTogether(ObjectAnimator.ofFloat(img_star, View.ALPHA, 0.0f, 1.0f).setDuration(2000),
                ObjectAnimator.ofFloat(voto_media, View.ALPHA, 0f, 1f).setDuration(2300),
                ObjectAnimator.ofFloat(img_budget, View.ALPHA, 0f, 1f).setDuration(2500),
                ObjectAnimator.ofFloat(icon_site, View.ALPHA, 0f, 1f).setDuration(3000),
                ObjectAnimator.ofFloat(icon_collection, View.ALPHA, 0f, 1f).setDuration(3300))
        }.start()
    }

    private fun setPoster() {

        img_poster.setPicasso(movieDb.posterPath, 3, img_erro = R.drawable.poster_empty)
        val posters = movieDb.images?.posters

        img_poster.setOnClickListener {
            if (posters != null && posters.isNotEmpty()) {
                val intent = Intent(requireActivity(), PosterGridActivity::class.java).apply {
                    putExtra(Constantes.POSTER, posters as Serializable)
                    putExtra(Constantes.NOME, movieDb.title)
                }
                val compat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(requireActivity(), img_poster, getString(R.string.poster_transition))
                ActivityCompat.startActivity(requireActivity(), intent, compat.toBundle())
            } else {
                requireActivity().makeToast(R.string.poster_empty)
            }
        }

        card_poster.setCardBackgroundColor(color)
    }

    private fun setProdutora() {
        movieDb.productionCompanies?.let {
            val production = movieDb.productionCompanies?.getOrNull(0)
            produtora.run {
                text = production?.name
                setTextColor(color)
                setOnClickListener {
                    production?.let {
                        startActivity(Intent(requireActivity(), ProdutoraActivity::class.java).apply {
                            putExtra(Constantes.PRODUTORA_ID, production.id)
                            putExtra(Constantes.ENDERECO, production.logoPath)
                            putExtra(Constantes.NOME, production.name)
                        })
                    }
                }
            }
        }
    }

    private fun getImdb() {
        movieDb.imdbId?.let { it ->
            subscriptions.add(Api(requireActivity()).getOmdbpi(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ imdb ->
                    if (view != null) {
                        imdbDd = imdb
                        setVotoMedia()
                    }
                }, {
                    if (view != null) {
                        voto_media.gone()
                        img_star.gone()
                        requireActivity().makeToast(R.string.ops)
                    }
                }))
        }
    }

    private fun setVotoMedia() {
        if (mediaNotas > 0) {
            img_star?.setImageResource(R.drawable.icon_star)
            val formatter = DecimalFormat("0.0")
            voto_media?.text = formatter.format(mediaNotas.toDouble())
        } else {
            img_star?.setImageResource(R.drawable.icon_star_off)
            voto_media?.setText(R.string.valor_zero)
            voto_media?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
        }
    }

    private fun setCategoria() {
        categoria_filme.text  = StringBuilder("").apply {
            movieDb.genres?.forEachIndexed { index, genero ->
                if (index != 0) append(" | ")
                append(genero?.name)
            }
        }.toString()
    }

    private fun setTitulo() {
        movieDb.title?.let {
            titulo_text?.text = it
        }
    }

    private fun setTimeFilme() {

        if (movieDb.runtime != null) {
            var horas = 0
            val minutos: Int
            var tempo = movieDb.runtime

            while (tempo!! > 60) {
                horas++
                tempo -= 60
            }
            minutos = tempo
            time_filme?.text = (horas.toString() + " " + getString(if (horas > 1) R.string.horas else R.string.hora) +
                " " + minutos + " " + getString(R.string.minutos))
        } else {
            time_filme?.text = getString(R.string.tempo_nao_informado)
        }
    }

    private fun setOriginalTitle() {
        if (movieDb.originalTitle != null) {
            original_title?.text = movieDb.originalTitle
        } else {
            original_title?.text = getString(R.string.original_title)
        }
    }

    private fun setSpokenLanguages() {
        movieDb.spokenLanguages?.let {
            if (it.isNotEmpty()) {
                movieDb.spokenLanguages.let {
                    spoken_languages.text = it?.get(0)?.name
                }
            } else {
                spoken_languages.text = getString(R.string.nao_informado)
            }
        }
    }

    private fun setProductionCountries() = if (movieDb.productionCountries?.isNotEmpty()!!) {
        production_countries.text = movieDb.productionCountries?.get(0)?.name
    } else {
        production_countries.text = getString(R.string.nao_informado)
    }

    private fun setPopularity() {

        val animatorCompat = ValueAnimator.ofFloat(1.0f, movieDb.popularity!!.toFloat())
        if (movieDb.popularity!! > 0) {

            animatorCompat.addUpdateListener { valueAnimator ->
                val valor = valueAnimator.animatedValue as Float
                var popularidade = valor.toString()

                if (popularidade[0] == '0' && isAdded) {
                    popularidade = popularidade.substring(2, popularidade.length)
                    popularity.text = "$popularidade  ${getString(R.string.mil)}"
                } else {
                    val posicao = popularidade.indexOf(".") + 2
                    popularidade = popularidade.substring(0, posicao)

                    if (isAdded) {
                        val milhoes: String = getString(R.string.milhoes)
                        popularidade += (" " + milhoes)
                        popularity.text = popularidade
                    }
                }
            }

            animatorCompat.duration = 900
            animatorCompat.setTarget(popularity)
            if (isAdded) {
                animatorCompat.start()
            }
        }
    }

    private fun setCast() {
        if (movieDb.credits?.cast!!.isNotEmpty() && isAdded) {
            recycle_filme_elenco?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                adapter = CastAdapter(requireActivity(), movieDb.credits?.cast ?: listOf())
            }
            recycle_filme_elenco.setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))

            textview_elenco?.visible()
        } else {
            textview_elenco?.gone()
            recycle_filme_elenco.apply {
                layoutParams.height = 1
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                isFocusable = false
            }
        }
    }

    private fun setCrews() {

        if (movieDb.credits?.crew!!.isNotEmpty() && isAdded) {

            recycle_filme_producao?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CrewAdapter(requireActivity(), movieDb.credits?.crew ?: listOf())
            }
            recycle_filme_producao.setScrollInvisibleFloatMenu(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu))

            textview_crews.visible()
        } else {
            textview_crews.gone()
            recycle_filme_producao.apply {
                layoutParams.height = 1
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                isFocusable = false
            }
        }
    }

    private fun setSimilares() {

        if (movieDb.similar?.resultsSimilar?.isNotEmpty()!!) {

            recycle_filme_similares?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = SimilaresFilmesAdapter(activity!!, movieDb.similar?.resultsSimilar)
            }

            recycle_filme_similares.setScrollInvisibleFloatMenu(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu))
            textview_similares.visible()
        } else {
            textview_similares.gone()
            recycle_filme_similares.apply {
                layoutParams.height = 1
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                isFocusable = false
            }
        }
    }

    private fun setLancamento() {

        if (movieDb.releaseDates?.resultsReleaseDates?.isNotEmpty()!!) {
            val date = movieDb.releaseDates?.resultsReleaseDates?.let {
                it.firstOrNull { releaseDateItem ->
                    releaseDateItem?.iso31661.equals(Locale.getDefault().country, ignoreCase = true)
                }
            }

            if (date != null) {
                date.releaseDates?.get(0)?.releaseDate?.let {
                    lancamento.text = it.parseDate()
                }
            } else {
                lancamento.text = getString(R.string.nao_informado)
            }
        } else {
            lancamento.text = getString(R.string.nao_informado)
        }
    }

    private fun setTrailer() {
            if (movieDb.videos?.results?.isNotEmpty()!!) {
                recycle_filme_trailer?.apply {
                    setHasFixedSize(true)
                    itemAnimator = DefaultItemAnimator()
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    adapter = TrailerAdapter(movieDb.videos?.results ?:  mutableListOf(), movieDb.overview
                        ?: "")
                }
                recycle_filme_trailer.setScrollInvisibleFloatMenu(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu))
            } else {
                recycle_filme_trailer.apply {
                    layoutParams.height = 1
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                    isFocusable = false
                }
            }
    }

    private fun setHome() {
        if (movieDb.homepage != null) {
            if (movieDb.homepage?.length!! > 5) {
                icon_site!!.setImageResource(R.drawable.site_on)
            } else {
                icon_site!!.setImageResource(R.drawable.site_off)
            }
        } else {
            icon_site!!.setImageResource(R.drawable.site_off)
        }
    }

    private fun setCollectoin() {
        if (movieDb.belongsToCollection != null) {

            icon_collection?.setImageResource(R.drawable.collection_on)
        } else {

            icon_collection?.setImageResource(R.drawable.collection_off)
        }
    }

    val mediaNotas: Float
        get() {
            var imdb = 0.0f
            var tmdb = 0.0f
            var metascore = 0.0f
            var tomato = 0.0f
            var tamanho = 0

            movieDb.voteAverage?.let {
                try {
                    tmdb = it
                    tamanho++
                } catch (e: Exception) {
                }
            }

            if (imdbDd != null) {
                if (imdbDd?.imdbRating != null) {
                    if (!imdbDd?.imdbRating!!.isEmpty()) {
                        try {
                            imdbDd?.let {
                                imdb = java.lang.Float.parseFloat(it.imdbRating)
                                tamanho++
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                if (imdbDd?.metascore != null) {
                    if (!imdbDd?.metascore!!.isEmpty()) {
                        try {
                            imdbDd?.let {
                                val meta = java.lang.Float.parseFloat(it.metascore)
                                val nota = meta / 10
                                metascore = nota
                                tamanho++
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                if (imdbDd?.tomatoRating != null) {
                    if (!imdbDd?.tomatoRating!!.isEmpty()) {
                        try {
                            imdbDd?.let {
                                tomato = java.lang.Float.parseFloat(it.tomatoRating)
                                tamanho++
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            return (tmdb + imdb + metascore + tomato) / tamanho
        }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}
