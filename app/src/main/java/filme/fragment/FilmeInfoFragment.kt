package filme.fragment


import activity.*
import adapter.CastAdapter
import adapter.CollectionPagerAdapter
import adapter.CrewAdapter
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import br.com.icaro.filme.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.API
import domain.Imdb
import domain.Movie
import domain.colecao.Colecao
import filme.adapter.SimilaresFilmesAdapter
import info.movito.themoviedbapi.model.Multi
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.fragment_container_filme.*
import produtora.activity.ProdutoraActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp
import java.text.DecimalFormat
import java.util.*

/**
 * Created by icaro on 03/07/16.
 */

class FilmeInfoFragment : android.support.v4.app.Fragment() {

    private var movieDb: Movie? = null
    private var imdbDd: Imdb? = null
    private lateinit var subscriptions: CompositeSubscription

    //************* Alguns metodos sendo chamados 2 vezes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val bundle = arguments
            movieDb = bundle.getSerializable(Constantes.FILME) as Movie
        }
        subscriptions = CompositeSubscription()
    }

    override fun onStart() {
        super.onStart()
        getImdb()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_container_filme, container, false)
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

        icon_reviews.setOnClickListener {
            if (movieDb?.imdbId != null) {
                val intent = Intent(context, ReviewsActivity::class.java)
                intent.putExtra(Constantes.NOME_FILME, movieDb?.title)
                intent.putExtra(Constantes.MEDIATYPE, "movie")
                val id = movieDb?.imdbId
                intent.putExtra(Constantes.FILME_ID, id)
                startActivity(intent)
            }
        }

        imdb_site?.setOnClickListener {
            val intent = Intent(activity, Site::class.java)
            intent.putExtra(Constantes.SITE,
                    "https:www.imdb.com/title/" + movieDb?.imdbId + "/")
            startActivity(intent)

        }

        tmdb_site!!.setOnClickListener {
            val intent = Intent(activity, Site::class.java)
            intent.putExtra(Constantes.SITE,
                    "https://www.themoviedb.org/movie/" + movieDb?.id + "/")
            startActivity(intent)

        }

//        netflix.setOnClickListener(View.OnClickListener {
//            if (netflixDados == null) {
//                return@OnClickListener
//            }
//
//            if (netflixDados!!.showId != 0) {
//                val url = "https://www.netflixDados.com/title/" + netflixDados?.showId
//                val webpage = Uri.parse(url)
//                val intent = Intent(Intent.ACTION_VIEW, webpage)
//                if (intent.resolveActivity(activity.packageManager) != null) {
//                    startActivity(intent)
//                }
//            } else {
//                val url = "https://www.netflixDados.com/search?q=" + movieDb?.title!!
//
//                val webpage = Uri.parse(url)
//                val intent = Intent(Intent.ACTION_VIEW, webpage)
//                if (intent.resolveActivity(activity.packageManager) != null) {
//                    startActivity(intent)
//                }
//            }
//        })

        img_budget.setOnClickListener {

            if (movieDb?.budget != null) {
                if (movieDb?.budget!! > 0) {

                    var valor = movieDb!!.budget.toString()
                    if (valor.length >= 6)
                        valor = valor.substring(0, valor.length - 6)
                    BaseActivity.SnackBar(activity.findViewById(R.id.fab_menu_filme),
                            getString(R.string.orcamento_budget) + " " +
                                    getString(R.string.dollar)
                                    + " " + valor + " " + getString(R.string.milhoes_budget))

                } else {
                    BaseActivity.SnackBar(activity.findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_budget))
                }
            }
        }

        icon_site.setOnClickListener {

            if (movieDb?.homepage !== "" && movieDb?.homepage != null) {
                val intent = Intent(context, Site::class.java)
                intent.putExtra(Constantes.SITE, movieDb!!.homepage)
                startActivity(intent)

            } else {
                BaseActivity.SnackBar(activity.findViewById(R.id.fab_menu_filme),
                        getString(R.string.no_site))
            }
        }


        img_star.setOnClickListener(onClickImageStar())


        icon_collection.setOnClickListener({
            if (movieDb?.belongsToCollection != null) {
                val inscricaoMovie = API(context = activity).getColecao(movieDb?.belongsToCollection?.id!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            getCollection(it)
                        }, { erro ->
                            Toast.makeText(activity, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        })

                subscriptions.add(inscricaoMovie)
            } else {
                BaseActivity.SnackBar(activity.findViewById(R.id.fab_menu_filme),
                        getString(R.string.sem_informacao_colletion))
            }
        })

        textview_elenco.setOnClickListener {
            val intent = Intent(context, ElencoActivity::class.java)
            intent.putExtra(Constantes.ID, movieDb?.id)
            intent.putExtra(Constantes.MEDIATYPE, Multi.MediaType.MOVIE)
            intent.putExtra(Constantes.NOME, movieDb?.title)
            startActivity(intent)

        }

        textview_crews.setOnClickListener {
            val intent = Intent(context, CrewsActivity::class.java)
            intent.putExtra(Constantes.ID, movieDb?.id)
            intent.putExtra(Constantes.MEDIATYPE, Multi.MediaType.MOVIE)
            intent.putExtra(Constantes.NOME, movieDb?.title)
            startActivity(intent)

        }

        textview_similares.setOnClickListener {
            val intent = Intent(context, SimilaresActivity::class.java)
            intent.putExtra(Constantes.FILME_ID, movieDb?.id)
            intent.putExtra(Constantes.NOME_FILME, movieDb?.title)
            startActivity(intent)

        }
    }

    private fun onClickImageStar(): View.OnClickListener? {
        return object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (mediaNotas > 0) {
                    val builder = AlertDialog.Builder(activity)
                    val inflater = activity.layoutInflater
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

//                    layout.findViewById<TextView>(R.id.nota_netflix)
//                            .text = (if (netflixDados?.rating != null)
//                        netflixDados?.rating + "/5"
//                    else
//                        "- -").toString() //TODO Netflix gone. Aguardando api netflix

//                    layout.findViewById<ImageView>(R.id.image_netflix)
//                            .setOnClickListener(View.OnClickListener {
//                                if (netflixDados == null) {
//                                    return@OnClickListener
//                                }
//
//                                if (netflixDados!!.showId != 0) {
//                                    val url = "https://www.netflixDados.com/title/" + netflixDados?.showId
//                                    val webpage = Uri.parse(url)
//                                    val intent = Intent(Intent.ACTION_VIEW, webpage)
//                                    startActivity(intent)
//                                    if (intent.resolveActivity(activity.packageManager) != null) {
//                                        startActivity(intent)
//                                    }
//                                }
//                            })

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

                                    var nome = imdbDd!!.title.replace(" ", "_").toLowerCase()
                                    nome = UtilsApp.removerAcentos(nome)
                                    val url = "https://www.rottentomatoes.com/m/" + nome
                                    val intent = Intent(activity, Site::class.java)
                                    intent.putExtra(Constantes.SITE, url)
                                    startActivity(intent)
                                }
                            })

                    layout.findViewById<ImageView>(R.id.image_imdb)
                            .setOnClickListener(OnClickListener@ {
                                if (imdbDd == null) {
                                    return@OnClickListener
                                }

                                if (imdbDd!!.type != null) {

                                    val url = "http://www.imdb.com/title/" + imdbDd!!.imdbID
                                    val intent = Intent(activity, Site::class.java)
                                    intent.putExtra(Constantes.SITE, url)
                                    startActivity(intent)
                                }
                            })

                    layout.findViewById<ImageView>(R.id.image_tmdb)
                            .setOnClickListener(View.OnClickListener {
                                if (movieDb == null) {
                                    return@OnClickListener
                                }
                                val url = "https://www.themoviedb.org/movie/" + movieDb!!.id!!
                                val intent = Intent(activity, Site::class.java)
                                intent.putExtra(Constantes.SITE, url)
                                startActivity(intent)
                            })

                    //REFAZER METODOS - MUITO GRANDE.

                    builder.setView(layout)
                    builder.show()

                } else {
                    BaseActivity.SnackBar(activity.findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_vote))

                }
            }
        }

    }

    private fun getCollection(colecao: Colecao?) {
        if (colecao?.parts?.size != 0) {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val dialog_collection = inflater.inflate(R.layout.dialog_collection, null)
            val pager = dialog_collection.findViewById<ViewPager>(R.id.viewpager_collection)
            pager.adapter = CollectionPagerAdapter(colecao, context)
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
        val alphaReviews = ObjectAnimator.ofFloat(icon_reviews, "alpha", 0f, 1f)
                .setDuration(2800)
        val alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0f, 1f)
                .setDuration(3000)
        val alphaCollecton = ObjectAnimator.ofFloat(icon_collection, "alpha", 0f, 1f)
                .setDuration(3300)
        animatorSet.playTogether(alphaStar, alphaBuget, alphaMedia, alphaReviews, alphaSite, alphaCollecton)
        animatorSet.start()
    }

    private fun setPoster() {

        if (movieDb?.posterPath != null) {
            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + movieDb?.posterPath!!)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(img_poster)

            img_poster.setOnClickListener {
                val intent = Intent(context, PosterGridActivity::class.java)
                val transition = getString(R.string.poster_transition)
                intent.putExtra(Constantes.FILME, movieDb)
                val compat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(activity, img_poster, transition)
                ActivityCompat.startActivity(activity, intent, compat.toBundle())

            }
        } else {
            img_poster!!.setImageResource(R.drawable.poster_empty)
        }
    }

    private fun setProdutora() {
        if (!movieDb?.productionCompanies?.isEmpty()!!) {

            produtora.text = movieDb?.productionCompanies?.get(0)?.name
            produtora.setTextColor(ContextCompat.getColor(context, R.color.primary))
            produtora.setOnClickListener {
                val intent = Intent(context, ProdutoraActivity::class.java)
                intent.putExtra(Constantes.PRODUTORA_ID, movieDb?.productionCompanies!![0]?.id)
                //intent.putExtra(Constantes.MEDIATYPE, Multi.MediaType.MOVIE) // Não usado
                startActivity(intent)
            }
        } else {
            label_produtora.visibility = View.GONE
        }
    }

    private fun getImdb() {
        if (movieDb?.imdbId != null) {
            val inscircaoImdb = API(activity).getOmdbpi(movieDb?.imdbId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        imdbDd = it
                        setVotoMedia()
                    }, { erro ->
                        Toast.makeText(activity, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
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
            voto_media?.setTextColor(ContextCompat.getColor(context, R.color.blue))
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
        if (movieDb!!.title != null) {
            titulo_text!!.text = movieDb!!.title
        }
    }

    private fun setTimeFilme() {

        if (movieDb?.runtime != null) {
            var horas = 0
            val minutos: Int
            var tempo = movieDb!!.runtime!!

            while (tempo > 60) {
                horas++
                tempo = tempo - 60
            }
            minutos = tempo
            time_filme!!.text = (horas.toString() + " " + getString(if (horas > 1) R.string.horas else R.string.hora)
                    + " " + minutos + " " + getString(R.string.minutos)).toString()//
        } else {
            time_filme!!.text = getString(R.string.tempo_nao_informado)
        }
    }

    private fun setOriginalTitle() {
        if (movieDb!!.originalTitle != null) {
            original_title!!.text = movieDb!!.originalTitle
        } else {
            original_title!!.text = getString(R.string.original_title)
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
            }

            textview_elenco!!.visibility = View.VISIBLE
            recycle_filme_elenco.adapter = CastAdapter(activity, movieDb?.credits?.cast)
        } else {
            recycle_filme_elenco.visibility = View.GONE
        }
    }

    private fun setCrews() {

        if (movieDb?.credits?.crew!!.isNotEmpty() && isAdded) {

            recycle_filme_producao?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }

            textview_crews.visibility = View.VISIBLE
            recycle_filme_producao.adapter = CrewAdapter(activity, movieDb?.credits?.crew)
        } else {
            recycle_filme_producao.visibility = View.GONE
        }
    }

    private fun setSimilares() {

        if (movieDb?.similar?.resultsSimilar?.isNotEmpty()!!) {

            recycle_filme_similares?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }

            recycle_filme_similares.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    when (newState) {
                        0 -> {
                            activity.fab_menu_filme.visibility = View.VISIBLE
                        }
                        1 -> {
                            activity.fab_menu_filme.visibility = View.INVISIBLE
                        }
                        2 -> {
                            activity.fab_menu_filme.visibility = View.INVISIBLE
                        }
                    }

                }
            })

            textview_similares.visibility = View.VISIBLE
            recycle_filme_similares.adapter = SimilaresFilmesAdapter(activity, movieDb?.similar?.resultsSimilar)
        } else {
            textview_similares.visibility = View.GONE
            recycle_filme_similares.visibility = View.GONE;
        }

    }

    private fun setLancamento() {

        if (movieDb?.releaseDates?.resultsReleaseDates?.isNotEmpty()!!) {

            val releases = movieDb?.releaseDates?.resultsReleaseDates
            lancamento.text = if (movieDb?.releaseDate?.length!! > 9) "movieDb?.releaseDate?.subSequence(0,10) ${Locale.getDefault().country}" else "N/A"
            releases?.forEach { date ->
                if (date?.iso31661 == Locale.getDefault().country) {
                    date?.releaseDates?.forEach({ it ->
                        if (it?.type == 1 || it?.type == 2 || it?.type == 3) {
                            lancamento.text = if (it?.releaseDate?.length!! > 9) "${movieDb?.releaseDate?.subSequence(0, 10)} ${Locale.getDefault().country}" else "N/A"
                        }
                    })
                }
            }

        }

    }

    private fun setTrailer() {

        recycle_filme_trailer?.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        }
        if (movieDb?.videos?.results?.isNotEmpty()!!) {
            val videos = movieDb?.videos?.results
            recycle_filme_trailer.adapter = TrailerAdapter(activity, videos, movieDb?.overview ?: "")
        } else {
            recycle_filme_trailer.setVisibility(View.GONE);
        }
    }

    private fun setHome() {
        if (movieDb?.homepage != null) {
            if (movieDb?.homepage?.length!! > 5) {
                // Log.d("SETHOME", movieDb.getHomepage());
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
                            imdb = java.lang.Float.parseFloat(imdbDd?.imdbRating)
                            tamanho++
                        } catch (e: Exception) {
                        }

                    }
                }

                if (imdbDd?.metascore != null) {
                    if (!imdbDd?.metascore!!.isEmpty()) {
                        try {
                            val meta = java.lang.Float.parseFloat(imdbDd?.metascore)
                            val nota = meta / 10
                            metascore = nota
                            tamanho++
                        } catch (e: Exception) {
                        }

                    }
                }

                if (imdbDd?.tomatoRating != null) {
                    if (!imdbDd?.tomatoRating!!.isEmpty()) {
                        try {
                            tomato = java.lang.Float.parseFloat(imdbDd?.tomatoRating)
                            tamanho++
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
