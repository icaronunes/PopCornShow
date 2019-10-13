package tvshow.fragment

import activity.BaseActivity
import adapter.CastAdapter
import adapter.CrewAdapter
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.in_production
import br.com.icaro.filme.R.string.mil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import configuracao.SettingsActivity
import domain.Api
import domain.Imdb
import domain.UserTvshow
import domain.tvshow.SeasonsItem
import domain.tvshow.Tvshow
import elenco.ElencoActivity
import fragment.FragmentBase
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbTvSeasons
import info.movito.themoviedbapi.model.tv.TvSeason
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.poster_tvhsow_details_layout.*
import kotlinx.android.synthetic.main.tvshow_info.*
import poster.PosterGridActivity
import producao.CrewsActivity
import produtora.activity.ProdutoraActivity
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import similares.SimilaresActivity
import site.Site
import temporada.TemporadaActivity
import tvshow.TemporadasAdapter
import tvshow.adapter.SimilaresSerieAdapter
import utils.*
import utils.ConstFirebase.ASSISTIDO
import utils.ConstFirebase.SEASONS
import utils.ConstFirebase.USEREPS
import utils.ConstFirebase.VISTO
import utils.Constantes.BASEMOVIEDB_TV
import utils.Constantes.IMDB
import utils.Constantes.METACRITICTV
import utils.Constantes.ROTTENTOMATOESTV
import utils.Constantes.SEGUINDO
import utils.UtilsApp.setEp
import utils.UtilsApp.setUserTvShow
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by icaro on 23/08/16.
 */

class TvShowFragment : FragmentBase() {

    private lateinit var recyclerViewTemporada: RecyclerView
    private var tipo: Int = 0
    private var color: Int = 0
    private var mediaNotas: Float = 0f
    private var seguindo: Boolean = false
    private lateinit var series: Tvshow
    private var mAuth: FirebaseAuth? = null
    private var myRef: DatabaseReference? = null
    private var userTvshow: UserTvshow? = null
    private var postListener: ValueEventListener? = null
    private var progressBarTemporada: ProgressBar? = null
    private var imdbDd: Imdb? = null


    companion object {

        fun newInstance(tipo: Int, series: Tvshow, color: Int, seguindo: Boolean): Fragment {
            val fragment = TvShowFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constantes.SERIE, series)
            bundle.putInt(Constantes.COLOR_TOP, color)
            bundle.putInt(Constantes.ABA, tipo)
            bundle.putSerializable(Constantes.USER, seguindo)
            fragment.arguments = bundle

            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            tipo = arguments?.getInt(Constantes.ABA)!!
            series = arguments?.getSerializable(Constantes.SERIE) as Tvshow
            color = arguments?.getInt(Constantes.COLOR_TOP)!!
            seguindo = arguments?.getBoolean(Constantes.USER)!!
        }
        //Validar se esta logado. Caso não, não precisa instanciar nada.
        subscriptions = CompositeSubscription()
        mAuth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().getReference("users")
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (tipo == R.string.informacoes) {
            isSeguindo()
            setSinopse()
            setTitulo()
            setCategoria()
            setLancamento()
            setProdutora()
            setHome()
            setOriginalTitle()
            setProductionCountries()
            setPopularity()
            setTemporada()
            setElenco()
            setProducao()
            setSimilares()
            setTrailer()
            setPoster()
            setStatus()
            setAnimacao()
            getImdb()
            setVotoMedia()
            setListeners()
            setNomeUltimoEp()
            setUltimoEpDate()
            setAdMob(adView)
        }
    }

    private fun setUltimoEpDate() {
        proximo_ep_date.let {
            it.text = series.lastEpisodeAir.airDate
        }
    }

    private fun setNomeUltimoEp() {
        series.lastEpisodeAir.let {
            ultimo_ep_name.text = it.name
        }
    }

    private fun setListeners() {
        icon_site?.setOnClickListener {
            if (!series.homepage.isNullOrBlank()) {
                startActivity(Intent(context, Site::class.java).apply {
                    putExtra(Constantes.SITE, series.homepage)
                })

            } else {
                BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                        getString(R.string.no_site))
            }
        }

        imdb_site?.setOnClickListener {
            series.external_ids?.imdbId?.let {
                startActivity(Intent(activity, Site::class.java).apply {
                    putExtra(Constantes.SITE, "${Constantes.IMDB}$it")
                })
            }
        }

        tmdb_site?.setOnClickListener {
            startActivity(Intent(activity, Site::class.java).apply {
                putExtra(Constantes.SITE, "${Constantes.BASEMOVIEDB_TV}${series.id}")
            })
        }

        img_star?.setOnClickListener {
            clickStarsNote()
        }
    }

    private fun clickStarsNote() {
        if (mediaNotas > 0) {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.layout_notas, null)

            imdbDd?.let { imdb ->
                imdb.imdbRating?.let {
                    (layout
                            ?.findViewById<View>(R.id.nota_imdb) as TextView).text = String.format(getString(R.string.bar_ten), it)
                }

                imdb.metascore?.let {
                    if (!it.contains("N/A"))
                        (layout.findViewById<View>(R.id.nota_metacritic) as TextView).text = String.format(getString(R.string.bar_hundred), it)
                }

                imdb.tomatoRating?.let {
                    if (!it.contains("N/A", true))
                        (layout.findViewById<View>(R.id.nota_tomatoes) as TextView).text = String.format(getString(R.string.bar_ten), it)
                }
            }

            series.voteAverage?.let {
                if (!it.equals(0.0))
                    (layout?.findViewById<View>(R.id.nota_tmdb) as TextView).text = String.format(getString(R.string.bar_ten), it)
            }

            layout.findViewById<View>(R.id.image_metacritic).setOnClickListener {
                imdbDd?.let {
                    val nome = it.title.replace(" ", "-").toLowerCase(Locale.ROOT).removerAcentos()
                    val url = "$METACRITICTV$nome"
                    startActivity(Intent(activity, Site::class.java).apply {
                        putExtra(Constantes.SITE, url)
                    })
                }
            }

            layout.findViewById<View>(R.id.image_tomatoes).setOnClickListener {
                imdbDd?.let {
                    val nome = it.title.replace(" ", "_").toLowerCase(Locale.ROOT).removerAcentos()
                    startActivity(Intent(activity, Site::class.java).apply {
                        putExtra(Constantes.SITE, "$ROTTENTOMATOESTV$nome")
                    })
                }
            }

            layout.findViewById<View>(R.id.image_imdb).setOnClickListener {
                startActivity(Intent(activity, Site::class.java).apply {
                    putExtra(Constantes.SITE, "$IMDB${imdbDd?.imdbID}")
                })
            }

            layout.findViewById<View>(R.id.image_tmdb).setOnClickListener {
                startActivity(Intent(activity, Site::class.java).apply {
                    putExtra(Constantes.SITE, "$BASEMOVIEDB_TV${series.id!!}")
                })
            }

            builder.setView(layout)
            builder.show()

        } else {
            BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                    getString(R.string.no_vote))
        }
    }

    private fun isSeguindo() {

        if (mAuth?.currentUser != null) {

            if (seguindo) {
                seguir?.setText(R.string.seguindo)
            } else {
                seguir?.setText(R.string.seguir)
            }
        } else {
            setStatusButton()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mAuth?.currentUser != null) {

            postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {
                        try {
                            userTvshow = dataSnapshot.getValue(UserTvshow::class.java)

                            if (getView() != null) {
                                recyclerViewTemporada = getView()?.rootView?.findViewById(R.id.temporadas_recycler) as RecyclerView
                                recyclerViewTemporada.adapter = TemporadasAdapter(activity!!, series, onClickListener(), color, userTvshow)
                                if (progressBarTemporada != null) {
                                    progressBarTemporada?.visibility = View.INVISIBLE
                                }
                            }
                        } catch (e: Exception) {
                            activity.let {
                                Toast.makeText(activity, R.string.ops_seguir_novamente, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        if (getView() != null) {
                            recyclerViewTemporada = getView()?.rootView?.findViewById(R.id.temporadas_recycler) as RecyclerView
                            recyclerViewTemporada.adapter = TemporadasAdapter(activity!!, series, onClickListener(), color, null)
                            if (progressBarTemporada != null) {
                                progressBarTemporada?.visibility = View.INVISIBLE
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }

            myRef?.child(mAuth?.currentUser!!
                    .uid)?.child("seguindo")?.child(series.id.toString())
                    ?.addValueEventListener(postListener!!)
        }
    }

    private fun setStatus() {
        series.status?.let {
            status?.setTextColor(color)

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val idioma = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)

            if (idioma) {
                when (series.status) {
                    "Returning Series" -> status?.setText(R.string.returnin_series)
                    "Ended" -> status!!.setText(R.string.ended)
                    "Canceled" -> status?.setText(R.string.canceled)
                    "In Production" -> status?.setText(in_production)
                    else -> status?.text = series.status
                }
            } else {
                status?.text = series.status
            }
        }
    }

    private fun setStatusButton() {
        seguir?.setTextColor(color)
        seguir?.isEnabled = false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val idioma = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
        if (idioma) {
            seguir?.text = resources.getText(R.string.sem_login)
        } else {
            seguir?.text = series.status
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        when (tipo) {

            R.string.temporadas -> {
                return getViewTemporadas(inflater, container)
            }
            R.string.informacoes -> {
                return getViewInformacoes(inflater, container)
            }
        }
        return null
    }

    private fun getViewTemporadas(inflater: LayoutInflater?, container: ViewGroup?): View {
        val view = inflater?.inflate(R.layout.temporadas, container, false)
        progressBarTemporada = view?.findViewById<View>(R.id.progressBarTemporadas) as ProgressBar
        recyclerViewTemporada = view.findViewById<View>(R.id.temporadas_recycler) as RecyclerView
        recyclerViewTemporada.setHasFixedSize(true)
        recyclerViewTemporada.itemAnimator = DefaultItemAnimator()
        recyclerViewTemporada.layoutManager = LinearLayoutManager(context)
        if (mAuth?.currentUser != null) {
            recyclerViewTemporada.adapter = TemporadasAdapter(activity!!, series, onClickListener(), color, userTvshow)
            if (progressBarTemporada != null) {
                progressBarTemporada?.visible()
            }
        } else {
            recyclerViewTemporada.adapter = TemporadasAdapter(activity!!, series, onClickListener(), color, null)
            if (progressBarTemporada != null) {
                progressBarTemporada?.invisible()
            }
        }

        return view
    }

    private fun setTemporada() {
        if (series.numberOfSeasons!! > 0) {
            temporadas?.text = series.numberOfSeasons.toString()
        }
    }

    private fun onClickListener(): TemporadasAdapter.TemporadasOnClickListener {
        return object : TemporadasAdapter.TemporadasOnClickListener {
            override fun onClickTemporada(view: View, position: Int, color: Int) {
                requireActivity().startActivity(Intent(context, TemporadaActivity::class.java).apply {
                    putExtra(Constantes.NOME, getString(R.string.temporada) + " " + series.seasons?.get(position)?.seasonNumber)
                    putExtra(Constantes.TEMPORADA_ID, series.seasons?.get(position)?.seasonNumber)
                    putExtra(Constantes.TEMPORADA_POSITION, position)
                    putExtra(Constantes.TVSHOW_ID, series.id)
                    putExtra(Constantes.COLOR_TOP, color)
                })
            }

            override fun onClickCheckTemporada(view: View, position: Int) {

                if (isVisto(position)) {
                    Toast.makeText(context, R.string.marcado_nao_assistido_temporada, Toast.LENGTH_SHORT).show()
                    val user = if (mAuth?.currentUser != null) mAuth?.currentUser?.uid else ""
                    val idSerie = series.id
                    val childUpdates = HashMap<String, Any>()

                    childUpdates["/$user/${SEGUINDO}/$idSerie/${SEASONS}/$position/${VISTO}"] = false
                    setStatusEps(position, false)
                    userTvshow?.seasons?.get(position)?.userEps?.forEachIndexed { index, _ ->
                        childUpdates["/$user/${SEGUINDO}/$idSerie/${SEASONS}/$position/${USEREPS}/$index/${ASSISTIDO}"] = false
                    }

                    myRef?.updateChildren(childUpdates)

                } else {
                    requireActivity().makeToast(R.string.marcado_assistido_temporada)
                    val user = if (mAuth?.currentUser != null) mAuth?.currentUser?.uid else ""
                    val idSerie = userTvshow?.id

                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/$user/${SEGUINDO}/$idSerie/${SEASONS}/$position/${VISTO}"] = true
                    setStatusEps(position, true)
                    userTvshow?.seasons?.get(position)?.userEps?.forEachIndexed { index, _ ->
                        childUpdates["/$user/${SEGUINDO}/$idSerie/${SEASONS}/$position/${USEREPS}/$index/${ASSISTIDO}"] = true
                    }
                    myRef?.updateChildren(childUpdates)
                }
            }
        }
    }


    private fun isVisto(position: Int): Boolean {
        return if (userTvshow?.seasons != null) {
            if (userTvshow?.seasons?.get(position) != null) {
                return userTvshow?.seasons?.get(position)?.isVisto!!
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun setStatusEps(position: Int, status: Boolean) {
        if (userTvshow != null) {
            if (userTvshow?.seasons?.get(position)?.userEps != null)
                for (i in 0 until userTvshow?.seasons?.get(position)?.userEps?.size!!) {
                    userTvshow?.seasons?.get(position)?.userEps?.get(position)?.isAssistido = status
                }
        }
    }

    private fun getViewInformacoes(inflater: LayoutInflater?, container: ViewGroup?): View? {
        val view = inflater?.inflate(R.layout.tvshow_info, container, false)
        view?.findViewById<Button>(R.id.seguir)?.setOnClickListener(onClickSeguir())

        return view
    }

    private fun onClickSeguir(): OnClickListener {
        return OnClickListener { view ->
            if (view != null) {
                progressBarTemporada = view.rootView?.findViewById<View>(R.id.progressBarTemporadas) as ProgressBar
                progressBarTemporada?.visible()
            }

            if (!seguindo) {
                seguindo = !seguindo
                isSeguindo()
                Thread(Runnable {
                    if (UtilsApp.isNetWorkAvailable(requireActivity())) {
                        val tvSeasons = TmdbApi(Config.TMDB_API_KEY).tvSeasons

                        userTvshow = setUserTvShow(series)

                        for (i in 0 until series.seasons?.size!!) {
                            val tvS: SeasonsItem? = series.seasons?.get(i)
                            val tvSeason: TvSeason = tvSeasons.getSeason(series.id!!, tvS?.seasonNumber!!, "en", TmdbTvSeasons.SeasonMethod.images)
                            userTvshow?.seasons?.get(i)?.userEps = setEp(tvSeason).toMutableList()
                        }

                        myRef?.child(if (mAuth?.currentUser != null) mAuth?.currentUser?.uid!! else "")
                                ?.child(SEGUINDO)
                                ?.child(series.id.toString())
                                ?.setValue(userTvshow)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        seguir?.setText(R.string.seguindo)
                                    } else {
                                        Toast.makeText(activity, R.string.erro_seguir, Toast.LENGTH_SHORT).show()
                                    }
                                }

                    }
                }).start()

            } else {

                val dialog = AlertDialog.Builder(context!!)
                        .setTitle(R.string.title_delete)
                        .setMessage(R.string.msg_parar_seguir)
                        .setNegativeButton(R.string.no, null)
                        .setOnDismissListener { progressBarTemporada?.visibility = View.GONE }
                        .setPositiveButton(R.string.ok) { _, _ ->
                            myRef?.child(if (mAuth?.currentUser != null) mAuth?.currentUser?.uid!! else "")
                                    ?.child(SEGUINDO)
                                    ?.child(series.id.toString())
                                    ?.removeValue()
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful)
                                            seguir?.setText(R.string.seguir)
                                    }
                            seguindo = !seguindo
                            isSeguindo()
                            progressBarTemporada?.visibility = View.GONE
                        }.create()

                dialog.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (myRef != null && mAuth?.currentUser != null) {
            myRef?.removeEventListener(postListener!!)
        }
        subscriptions.unsubscribe()
    }

    private fun setSinopse() {

        if (series.overview.isNullOrBlank()) {
            descricao.text = getString(R.string.sem_sinopse)
        } else {
            descricao.text = series.overview
        }
    }

    private fun setAnimacao() {
        AnimatorSet().apply {
            playTogether(
                    ObjectAnimator.ofFloat(img_star, View.ALPHA, 0.0f, 1.0f).setDuration(2000),
                    ObjectAnimator.ofFloat(voto_media, View.ALPHA, 0.0f, 1.0f).setDuration(2300),
                    ObjectAnimator.ofFloat(icon_site, View.ALPHA, 0.0f, 1.0f).setDuration(3000)
            )
        }.start()
    }

    private fun setPoster() {
        series.posterPath?.let {
            img_poster.setPicasso(it, 2, img_erro = R.drawable.poster_empty)
            img_poster?.setOnClickListener {
                val intent = Intent(context, PosterGridActivity::class.java).apply {
                    putExtra(Constantes.POSTER, series.images?.posters as Serializable)
                    putExtra(Constantes.NOME, series.name)
                }
                val compat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(requireActivity(),
                                img_poster,
                                getString(R.string.poster_transition))
                ActivityCompat.startActivity(requireActivity(), intent, compat.toBundle())
            }
        }
        card_poster.setCardBackgroundColor(color)
    }

    private fun setProdutora() {
        series.productionCompanies?.let {
            val production = series.productionCompanies?.getOrNull(0)
            produtora?.setTextColor(color)
            produtora?.text = production?.name

            produtora?.setOnClickListener {
                if (production != null) {
                    context?.startActivity(Intent(context, ProdutoraActivity::class.java).apply {
                        putExtra(Constantes.PRODUTORA_ID, production.id)
                    })
                } else {
                    Toast.makeText(context, getString(R.string.sem_informacao_company), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setCategoria() {
        StringBuilder().apply {
            series.genres?.forEach {
                this.append(" | ${it?.name}")
            }
            categoria_tvshow?.text = toString()
        }
    }

    private fun setVotoMedia() {
        mediaNotas = getMedia()
        if (mediaNotas > 0) {
            img_star?.setImageResource(R.drawable.icon_star)
            val formatter = DecimalFormat("0.0")
            formatter.format(mediaNotas)?.let {
                voto_media?.text = it
                frame_nota.contentDescription = it
            }
        } else {
            img_star?.setImageResource(R.drawable.icon_star_off)
            voto_media?.setText(R.string.valor_zero)
            voto_media?.setTextColor(ContextCompat.getColor(context!!, R.color.blue))
        }
    }

    private fun setTitulo() {
        series.name?.let {
            titulo_tvshow?.text = it
        }
    }

    private fun setOriginalTitle() {
        if (series.originalName != null) {
            original_title?.text = series.originalName
        } else {
            original_title?.text = getString(R.string.original_title)
        }
    }

    private fun setProductionCountries() {
        series.originCountry?.getOrNull(0)?.let {
            production_countries?.text = it
        }
    }

    private fun setPopularity() {
        //Todo refazer metodo
        if (series.popularity != null) {
            ValueAnimator.ofFloat(1.0f, series.popularity!!.toFloat()).apply {
                addUpdateListener { valueAnimator ->
                    val valor = valueAnimator.animatedValue as Float
                    var popularidade = valor.toString()

                    if (popularidade[0] == '0' && isAdded) {
                        popularidade = popularidade.substring(2, popularidade.length)
                        popularity?.text = "$popularidade  ${getString(mil)}"

                    } else {

                        val posicao = popularidade.indexOf(".") + 2
                        popularidade = popularidade.substring(0, posicao)
                        var milhoes = ""
                        if (isAdded) {
                            milhoes = getString(R.string.milhoes)
                        }
                        popularidade += (" $milhoes")
                        popularity?.text = popularidade
                    }
                }
                duration = 900
                setTarget(popularity)
            }.start()
        }
    }

    private fun setElenco() {

        textview_elenco?.setOnClickListener {
            startActivity(Intent(context, ElencoActivity::class.java).apply {
                putExtra(Constantes.ELENCO, series.credits?.cast as Serializable)
                putExtra(Constantes.NOME, series.name)
            })
        }

        if (series.credits?.cast?.isNotEmpty()!!) {
            textview_elenco?.visible()
            recycle_tvshow_elenco?.setHasFixedSize(true)
            recycle_tvshow_elenco?.itemAnimator = DefaultItemAnimator()
            recycle_tvshow_elenco?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recycle_tvshow_elenco.adapter = CastAdapter(activity, series.credits?.cast)
        } else {
            textview_elenco.gone()
            recycle_tvshow_elenco.layoutParams.height = 1
        }
    }

    private fun setProducao() {

        textview_crews?.setOnClickListener {
            startActivity(  Intent(context, CrewsActivity::class.java).apply {
                putExtra(Constantes.PRODUCAO, series.credits?.crew as Serializable)
                putExtra(Constantes.NOME, series.name)
            })
        }

        if (series.credits?.crew?.isNotEmpty()!!) {
            textview_crews?.visibility = View.VISIBLE
            recycle_tvshow_producao.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = CrewAdapter(activity, series.credits?.crew)
            }
        } else {
            textview_crews.gone()
            recycle_tvshow_producao.layoutParams.height = 1
        }
    }

    private fun setSimilares() {
        text_similares.setOnClickListener {
            startActivity(Intent(context, SimilaresActivity::class.java).apply {
                putExtra(Constantes.SIMILARES_TVSHOW, series.similar?.results as Serializable)
                putExtra(Constantes.NOME, series.name)
            })
        }

        if (series.similar?.results?.isNotEmpty()!!) {
            recycle_tvshow_similares?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                adapter = SimilaresSerieAdapter(requireActivity(), series.similar?.results)
            }
            //Todo - faz um metodo generico para usar em todos os recycler
            recycle_tvshow_similares.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        0 -> {
                            activity?.fab_menu_filme?.visibility = View.VISIBLE
                        }
                        1 -> {
                            activity?.fab_menu_filme?.visibility = View.INVISIBLE
                        }
                        2 -> {
                            activity?.fab_menu_filme?.visibility = View.INVISIBLE
                        }
                    }
                }
            })

            text_similares.visible()

        } else {
            text_similares.gone()
            recycle_tvshow_similares.layoutParams.height = 1
        }
    }

    private fun setLancamento() {
        var inicio = ""
        if (series.firstAirDate != null) {
            inicio = series.firstAirDate?.subSequence(0, 4) as String
        }
        if (series.lastAirDate != null) {
            lancamento?.text = "$inicio - ${series.lastAirDate?.substring(0, 4)}"
        } else {
            lancamento?.text = getString(R.string.nao_informado)
        }
    }

    private fun setTrailer() {
        if (series.videos?.results?.isNotEmpty()!!) {
            recycle_tvshow_trailer.apply {
                recycle_tvshow_trailer?.setHasFixedSize(true)
                recycle_tvshow_trailer?.itemAnimator = DefaultItemAnimator()
                recycle_tvshow_trailer?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recycle_tvshow_trailer.adapter =  TrailerAdapter(activity, series.videos?.results, series.overview)
            }
        }
    }

    private fun setHome() {
        if (series.homepage != null) {
            if (series.homepage?.length!! > 5) {
                icon_site?.setImageResource(R.drawable.site_on)
            } else {
                icon_site?.setImageResource(R.drawable.site_off)
            }
        } else {
            icon_site?.setImageResource(R.drawable.site_off)
        }
    }

    fun getImdb() {
        subscriptions.add(Api(context!!).getOmdbpi(series.external_ids?.imdbId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Imdb> {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        requireActivity().makeToast(R.string.ops)
                    }

                    override fun onNext(imdb: Imdb) {
                        imdbDd = imdb
                    }
                }))
    }

    private fun getMedia(): Float {
        //Todo - refazer metodo
        var imdb = 0f
        var tmdb = 0f
        var metascore = 0f
        var tomato = 0f
        var tamanho = 0

        if (series.voteAverage != null)
            if (series.voteAverage!! > 0) {
                try {
                    tmdb = series.voteAverage!!.toFloat()
                    tamanho++
                } catch (e: Exception) {

                }
            }

        if (imdbDd != null) {

            if (imdbDd?.imdbRating.isNullOrBlank()) {
                try {
                    imdb = java.lang.Float.parseFloat(imdbDd?.imdbRating!!)
                    tamanho++
                } catch (e: Exception) {

                }
            }

            if (imdbDd?.metascore.isNullOrEmpty()) {

                try {
                    val meta = java.lang.Float.parseFloat(imdbDd?.metascore!!)
                    val nota = meta / 10
                    metascore = nota
                    tamanho++
                } catch (e: Exception) {
                }


            }

            if (imdbDd?.tomatoRating.isNullOrEmpty()) {

                try {
                    tomato = java.lang.Float.parseFloat(imdbDd?.tomatoRating!!)
                    tamanho++
                } catch (e: Exception) {
                }
            }
        }
        return (tmdb + imdb + metascore + tomato) / tamanho
    }
}



