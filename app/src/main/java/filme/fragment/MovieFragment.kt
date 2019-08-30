package filme.fragment


import activity.BaseActivity
import site.Site
import adapter.CastAdapter
import filme.adapter.CollectionPagerAdapter
import adapter.CrewAdapter
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.Api
import domain.Imdb
import domain.Movie
import domain.colecao.PartsItem
import elenco.ElencoActivity
import filme.adapter.SimilaresFilmesAdapter
import fragment.FragmentBase
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.filme_info.*
import poster.PosterGridActivity
import producao.CrewsActivity
import produtora.activity.ProdutoraActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import similares.SimilaresActivity
import utils.Constantes
import utils.UtilsApp
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*

/**
 * Created by icaro on 03/07/16.
 */

class MovieFragment : FragmentBase() {

    private var movieDb: Movie? = null
    private var imdbDd: Imdb? = null
    private var color: Int = 0
    //  private lateinit var subscriptions: CompositeSubscription

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

        return inflater.inflate(R.layout.filme_info, container, false)
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
        setAnimacao()
        setStatus()
        setAdMob(adView)

        imdb_site.setOnClickListener {
            val intent = Intent(activity, Site::class.java)
            intent.putExtra(Constantes.SITE,
                    "${Constantes.IMDB}${movieDb?.imdbId}/")
            startActivity(intent)
        }

        tmdb_site?.setOnClickListener {
            val intent = Intent(activity, Site::class.java)
            intent.putExtra(Constantes.SITE,
                    "${Constantes.BASEMOVIEDB_MOVIE}${movieDb?.id}/")
            startActivity(intent)
        }

        img_budget?.setOnClickListener {

            if (movieDb?.budget != null) {
                if (movieDb?.budget!! > 0) {

                    var valor = movieDb!!.budget.toString()
                    if (valor.length >= 6)
                        valor = valor.substring(0, valor.length - 6)
                    BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                            getString(R.string.orcamento_budget) + " " +
                                    getString(R.string.dollar)
                                    + " " + valor + " " + getString(R.string.milhoes_budget))

                } else {
                    BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_budget))
                }
            }
        }

        icon_site?.setOnClickListener {

            if (movieDb?.homepage !== "" && movieDb?.homepage != null) {
                val intent = Intent(context, Site::class.java)
                intent.putExtra(Constantes.SITE, movieDb!!.homepage)
                startActivity(intent)

            } else {
                BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                        getString(R.string.no_site))
            }
        }


        img_star.setOnClickListener(onClickImageStar())

        icon_collection.setOnClickListener {
            if (movieDb?.belongsToCollection != null) {
                val inscricaoMovie = Api(context = context!!).getColecao(movieDb?.belongsToCollection?.id!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            getCollection(it?.parts?.sortedBy { it?.releaseDate })
                        }, { _ ->
                            Toast.makeText(activity, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        })

                subscriptions.add(inscricaoMovie)
            } else {
                BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                        getString(R.string.sem_informacao_colletion))
            }
        }

        textview_elenco.setOnClickListener {
            val intent = Intent(context, ElencoActivity::class.java)
            intent.putExtra(Constantes.ELENCO, movieDb?.credits?.cast as Serializable)
            intent.putExtra(Constantes.NOME, movieDb?.title)
            startActivity(intent)

        }

        textview_crews.setOnClickListener {
            val intent = Intent(context, CrewsActivity::class.java)
            intent.putExtra(Constantes.PRODUCAO, movieDb?.credits?.crew as Serializable)
            intent.putExtra(Constantes.NOME, movieDb?.title)
            startActivity(intent)

        }

        textview_similares.setOnClickListener {
            val intent = Intent(context, SimilaresActivity::class.java)
            intent.putExtra(Constantes.SIMILARES_FILME, movieDb?.similar?.resultsSimilar as Serializable)
            intent.putExtra(Constantes.NOME, movieDb?.title)
            startActivity(intent)

        }
    }

    private fun setStatus() {
        movieDb?.status.let {
            status.text = it
            status.setTextColor(color)
        }
    }

    private fun onClickImageStar(): View.OnClickListener? {
        return object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (mediaNotas > 0) {
                    val builder = AlertDialog.Builder(activity)
                    val inflater = activity?.layoutInflater!!
                    val layout = inflater.inflate(R.layout.layout_notas, null)

                    if (imdbDd != null) {
                        layout.findViewById<TextView>(R.id.nota_imdb)
                                .text = if (imdbDd?.imdbRating != null)
                            imdbDd?.imdbRating + "/10"
                        else
                            "- -"

                        layout.findViewById<TextView>(R.id.nota_metacritic)
                                .text = if (imdbDd?.metascore != null)
                            imdbDd?.metascore + "/100"
                        else
                            "- -"

                        layout.findViewById<TextView>(R.id.nota_tomatoes)
                                .text = if (imdbDd?.tomatoRating != null)
                            imdbDd?.tomatoRating + "/10"
                        else
                            "- -"
                    }

                    layout.findViewById<TextView>(R.id.nota_tmdb)
                            .text = if (!movieDb?.voteAverage!!.equals(0.0))
                        movieDb?.voteAverage!!.toString() + "/10"
                    else
                        "- -"


                    layout.findViewById<ImageView>(R.id.image_metacritic)
                            .setOnClickListener(View.OnClickListener {
                                if (imdbDd == null) {
                                    return@OnClickListener
                                }

                                if (imdbDd!!.type != null) {

                                    var nome = imdbDd!!.title.replace(" ", "-").toLowerCase()
                                    nome = UtilsApp.removerAcentos(nome)
                                    val url = "http://www.metacritic.com/movie/" + nome

                                    val intent = Intent(activity, Site::class.java)
                                    intent.putExtra(Constantes.SITE, url)
                                    startActivity(intent)
                                }
                            })

                    layout.findViewById<ImageView>(R.id.image_tomatoes)
                            .setOnClickListener(View.OnClickListener {
                                if (imdbDd == null) {
                                    return@OnClickListener
                                }

                                if (imdbDd?.type != null) {

                                    var nome = imdbDd?.title?.replace(" ", "_")?.toLowerCase()
                                    nome = UtilsApp.removerAcentos(nome)
                                    val url = "https://www.rottentomatoes.com/m/" + nome
                                    val intent = Intent(activity, Site::class.java)
                                    intent.putExtra(Constantes.SITE, url)
                                    startActivity(intent)
                                }
                            })

                    layout.findViewById<ImageView>(R.id.image_imdb)
                            .setOnClickListener OnClickListener@{
                                if (imdbDd == null) {
                                    return@OnClickListener
                                }

                                if (imdbDd?.imdbID != null) {

                                    val url = "http://www.imdb.com/title/" + imdbDd?.imdbID
                                    val intent = Intent(activity, Site::class.java)
                                    intent.putExtra(Constantes.SITE, url)
                                    startActivity(intent)
                                }
                            }

                    layout.findViewById<ImageView>(R.id.image_tmdb)
                            .setOnClickListener(View.OnClickListener {
                                if (movieDb == null) {
                                    return@OnClickListener
                                }
                                val url = "https://www.themoviedb.org/movie/" + movieDb?.id!!
                                val intent = Intent(activity, Site::class.java)
                                intent.putExtra(Constantes.SITE, url)
                                startActivity(intent)
                            })

                    //REFAZER METODOS - MUITO GRANDE.

                    builder.setView(layout)
                    builder.show()

                } else {
                    BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_vote))

                }
            }
        }

    }

    private fun getCollection(colecao: List<PartsItem?>?) {
        if (colecao?.isNotEmpty()!!) {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity?.layoutInflater
            val dialog_collection = inflater?.inflate(R.layout.dialog_collection, null)
            val pager = dialog_collection?.findViewById<ViewPager>(R.id.viewpager_collection)
            pager?.adapter = CollectionPagerAdapter(colecao, context!!)
            builder.setView(dialog_collection)
            builder.show()

        } else {
            Toast.makeText(context, R.string.sem_informacao_colletion, Toast.LENGTH_SHORT).show()
        }

    }

    fun setSinopse() {

        if (movieDb?.overview != null) {

            descricao.text = movieDb?.overview
        } else {
            descricao.text = getString(R.string.sem_sinopse)
        }
    }

    val locale: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.getDefault().toLanguageTag()
        } else {
            Locale.getDefault().language + "-" + Locale.getDefault().country
        }

    fun setBuget() {

        if (movieDb?.budget!! > 0) {
            img_budget.setImageResource(R.drawable.orcamento)
        } else {
            img_budget.setImageResource(R.drawable.sem_orcamento)

        }

    }

    fun setAnimacao() {
        val animatorSet = AnimatorSet()
        val alphaStar = ObjectAnimator.ofFloat(img_star, "alpha", 0.0f, 1.0f)
                .setDuration(2000)
        val alphaMedia = ObjectAnimator.ofFloat(voto_media, "alpha", 0f, 1f)
                .setDuration(2300)
        val alphaBuget = ObjectAnimator.ofFloat(img_budget, "alpha", 0f, 1f)
                .setDuration(2500)
        val alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0f, 1f)
                .setDuration(3000)
        val alphaCollecton = ObjectAnimator.ofFloat(icon_collection, "alpha", 0f, 1f)
                .setDuration(3300)
        animatorSet.playTogether(alphaStar, alphaBuget, alphaMedia, alphaSite, alphaCollecton)
        animatorSet.start()
    }

    private fun setPoster() {

        if (movieDb?.posterPath != null) {
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + movieDb?.posterPath)
                    .into(img_poster)

            img_poster.setOnClickListener {
                val intent = Intent(context, PosterGridActivity::class.java)
                val transition = getString(R.string.poster_transition)
                intent.putExtra(Constantes.POSTER, movieDb?.images?.posters as Serializable)
                intent.putExtra(Constantes.NOME, movieDb?.title)
                val compat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(activity!!, img_poster, transition)
                ActivityCompat.startActivity(activity!!, intent, compat.toBundle())

            }
        } else {
            img_poster!!.setImageResource(R.drawable.poster_empty)
        }
    }

    private fun setProdutora() {
        if (movieDb?.productionCompanies?.isNotEmpty()!!) {
            produtora.text = movieDb?.productionCompanies?.get(0)?.name
            produtora.setTextColor(ContextCompat.getColor(context!!, R.color.primary))
            produtora.setOnClickListener {
                movieDb?.productionCompanies?.get(0)?.let { production ->
                    startActivity(Intent(context, ProdutoraActivity::class.java).apply {
                        putExtra(Constantes.PRODUTORA_ID, production.id)
                        putExtra(Constantes.ENDERECO, production.logoPath)
                        putExtra(Constantes.NOME, production.name)
                    })
                }
            }
        } else {
            label_produtora.visibility = View.GONE
        }
    }

    private fun getImdb() {
        if (movieDb?.imdbId != null) {
            val inscircaoImdb = Api(context!!).getOmdbpi(movieDb?.imdbId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (view != null) {
                            imdbDd = it
                            setVotoMedia()
                        }
                    }, {
                        if (view != null) {
                            Toast.makeText(activity, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        }
                    })
            subscriptions.add(inscircaoImdb)
        }
    }

    private fun setVotoMedia() {
        val nota = mediaNotas
        if (nota > 0) {
            img_star?.setImageResource(R.drawable.icon_star)
            val formatter = DecimalFormat("0.0")
            voto_media?.text = formatter.format(nota.toDouble())

        } else {
            img_star?.setImageResource(R.drawable.icon_star_off)
            voto_media?.setText(R.string.valor_zero)
            voto_media?.setTextColor(ContextCompat.getColor(context!!, R.color.blue))
        }
    }

    private fun setCategoria() {

        val stringBuilder = StringBuilder("")

        movieDb?.genres?.forEach { genero ->
            stringBuilder.append(" | " + genero?.name)
        }

        categoria_filme.text = stringBuilder.toString()
    }

    private fun setTitulo() {
        if (movieDb?.title != null) {
            titulo_text?.text = movieDb?.title
        }
    }

    private fun setTimeFilme() {

        if (movieDb?.runtime != null) {
            var horas = 0
            val minutos: Int
            var tempo = movieDb!!.runtime!!

            while (tempo > 60) {
                horas++
                tempo -= 60
            }
            minutos = tempo
            time_filme?.text = (horas.toString() + " " + getString(if (horas > 1) R.string.horas else R.string.hora)
                    + " " + minutos + " " + getString(R.string.minutos)).toString()//
        } else {
            time_filme?.text = getString(R.string.tempo_nao_informado)
        }
    }

    private fun setOriginalTitle() {
        if (movieDb?.originalTitle != null) {
            original_title?.text = movieDb!!.originalTitle
        } else {
            original_title?.text = getString(R.string.original_title)
        }

    }

    private fun setSpokenLanguages() {
        if (movieDb?.spokenLanguages?.isNotEmpty()!!) {
            val languages = movieDb?.spokenLanguages
            spoken_languages.text = languages?.get(0)?.name
        } else {
            spoken_languages.text = getString(R.string.não_informado)
        }
    }

    private fun setProductionCountries() = if (movieDb?.productionCountries?.isNotEmpty()!!) {

        production_countries.text = movieDb?.productionCountries?.get(0)?.name
    } else {
        production_countries.text = getString(R.string.não_informado);
    }

    private fun setPopularity() {

        val animatorCompat = ValueAnimator.ofFloat(1.0f, movieDb?.popularity!!.toFloat())
        if (movieDb?.popularity!! > 0) {

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
            //animatorCompat.setTarget(voto_quantidade);
            animatorCompat.setTarget(popularity)
            if (isAdded) {
                animatorCompat.start()
            }
        }

    }

    private fun setCast() {
        if (movieDb?.credits?.cast!!.isNotEmpty() && isAdded) {

            recycle_filme_elenco?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CastAdapter(activity, movieDb?.credits?.cast)
            }

            textview_elenco?.visibility = View.VISIBLE
        } else {
            recycle_filme_elenco.layoutParams.height = 1
        }
    }

    private fun setCrews() {

        if (movieDb?.credits?.crew!!.isNotEmpty() && isAdded) {

            recycle_filme_producao?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CrewAdapter(activity, movieDb?.credits?.crew)
            }
            textview_crews.visibility = View.VISIBLE
        } else {
            recycle_filme_producao.layoutParams.height = 1
        }
    }

    private fun setSimilares() {

        if (movieDb?.similar?.resultsSimilar?.isNotEmpty()!!) {

            recycle_filme_similares?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = SimilaresFilmesAdapter(activity!!, movieDb?.similar?.resultsSimilar)
            }

            recycle_filme_similares.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        0 -> {
                            fab_menu_filme?.visibility = View.VISIBLE
                        }
                        1 -> {
                            fab_menu_filme?.visibility = View.INVISIBLE
                        }
                        2 -> {
                            fab_menu_filme?.visibility = View.INVISIBLE
                        }
                    }

                }
            })

            textview_similares.visibility = View.VISIBLE
        } else {
            textview_similares.visibility = View.GONE
            recycle_filme_similares.layoutParams.height = 1
        }
    }

    private fun setLancamento() {

        if (movieDb?.releaseDates?.resultsReleaseDates?.isNotEmpty()!!) {

            val releases = movieDb?.releaseDates?.resultsReleaseDates
            lancamento.text = if (movieDb?.releaseDate?.length!! > 9) "${movieDb?.releaseDate?.subSequence(0, 10)} ${Locale.getDefault().country}" else "N/A"
            releases?.forEach { date ->
                if (date?.iso31661 == Locale.getDefault().country) {
                    date?.releaseDates?.forEach { it ->
                        if (it?.type == 1 || it?.type == 2 || it?.type == 3) {
                            lancamento.text = if (it.releaseDate?.length!! > 9) "${movieDb?.releaseDate?.subSequence(0, 10)} ${Locale.getDefault().country}" else "N/A"
                        }
                    }
                }
            }
        }
    }

    private fun setTrailer() {

        if (movieDb?.videos?.results?.isNotEmpty()!!) {
            recycle_filme_trailer?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = TrailerAdapter(activity, movieDb?.videos?.results, movieDb?.overview
                        ?: "")
            }
        } else {
            recycle_filme_trailer.layoutParams.height = 1
        }
    }

    private fun setHome() {
        if (movieDb?.homepage != null) {
            if (movieDb?.homepage?.length!! > 5) {
                icon_site!!.setImageResource(R.drawable.site_on)
            } else {
                icon_site!!.setImageResource(R.drawable.site_off)
            }
        } else {
            icon_site!!.setImageResource(R.drawable.site_off)
        }
    }

    private fun setCollectoin() {
        if (movieDb?.belongsToCollection != null) {

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

            if (movieDb != null)
                if (movieDb?.voteAverage!! > 0) {
                    try {
                        tmdb = movieDb?.voteAverage!!
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