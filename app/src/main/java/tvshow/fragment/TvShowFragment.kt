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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
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
import utils.Constantes.METACRITICTV
import utils.Constantes.ROTTENTOMATOESTV
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
            if (series.homepage.isNullOrBlank()) {
                val intent = Intent(context, Site::class.java)
                intent.putExtra(Constantes.SITE, series.homepage)
                startActivity(intent)

            } else {
                BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                        getString(R.string.no_site))
            }
        }

        imdb_site?.setOnClickListener {
            if (series.external_ids?.imdbId != null) {
                val intent = Intent(activity, Site::class.java)
                intent.putExtra(Constantes.SITE,
                        "${Constantes.IMDB}${series.external_ids?.imdbId}")
                startActivity(intent)

            }
        }

        tmdb_site?.setOnClickListener {
            val intent = Intent(activity, Site::class.java)
            intent.putExtra(Constantes.SITE,
                    "${Constantes.BASEMOVIEDB_TV}${series.id}")
            startActivity(intent)
        }

        img_star?.setOnClickListener {
            if (mediaNotas > 0) {
                val builder = AlertDialog.Builder(activity!!)
                val inflater = activity?.layoutInflater
                val layout = inflater?.inflate(R.layout.layout_notas, null)


                if (imdbDd != null) {
                    (layout
                            ?.findViewById<View>(R.id.nota_imdb) as TextView).text = if (imdbDd?.imdbRating != null)
                        imdbDd?.imdbRating + "/10"
                    else
                        "- -"
                    (layout
                            .findViewById<View>(R.id.nota_metacritic) as TextView).text = if (imdbDd?.metascore != null)
                        imdbDd?.metascore + "/100"
                    else
                        "- -"
                    (layout
                            .findViewById<View>(R.id.nota_tomatoes) as TextView).text = if (imdbDd?.tomatoRating != null)
                        imdbDd?.tomatoRating + "/10"
                    else
                        "- -"
                }

                    (layout
                            ?.findViewById<View>(R.id.nota_tmdb) as TextView).text = (if (series.voteAverage != 0.0)
                        series.voteAverage?.toString() + "/10"
                    else
                        "- -").toString()

                (layout.findViewById<View>(R.id.image_metacritic) as ImageView).setOnClickListener {
                    imdbDd?.let {
                        val nome = it.title.replace(" ", "-").toLowerCase(Locale.ROOT).removerAcentos()
                        val url = "$METACRITICTV$nome"
                        startActivity(Intent(activity, Site::class.java).apply {
                            putExtra(Constantes.SITE, url)
                        })
                    }
                }

                (layout.findViewById<View>(R.id.image_tomatoes) as ImageView).setOnClickListener {
                    imdbDd?.let {
                        val nome = it.title.replace(" ", "_").toLowerCase(Locale.ROOT).removerAcentos()
                        val intent = Intent(activity, Site::class.java)
                        intent.putExtra(Constantes.SITE, "$ROTTENTOMATOESTV$nome")
                        startActivity(intent)
                    }
                }

                (layout.findViewById<View>(R.id.image_imdb) as ImageView).setOnClickListener(OnClickListener {
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

                (layout.findViewById<View>(R.id.image_tmdb) as ImageView).setOnClickListener(OnClickListener {
                    if (series == null) {
                        return@OnClickListener
                    }
                    val url = "https://www.themoviedb.org/tv/" + series.id!!
                    val intent = Intent(activity, Site::class.java)
                    intent.putExtra(Constantes.SITE, url)
                    startActivity(intent)
                })


                builder.setView(layout)
                builder.show()

            } else {
                BaseActivity.SnackBar(activity?.findViewById(R.id.fab_menu_filme),
                        getString(R.string.no_vote))
            }
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
        if (series.status != null) {
            status?.setTextColor(color)

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)

            if (idioma_padrao) {

                when (series.status) {
                    "Returning Series" -> status?.setText(R.string.returnin_series)
                    "Ended" -> status!!.setText(R.string.ended)
                    "Canceled" -> status?.setText(R.string.canceled)
                    "In Production" -> status?.setText(in_production)
                    else -> status?.text = series.status
                }
            }
        }

    }

    private fun setStatusButton() {
        seguir?.setTextColor(color)
        seguir?.isEnabled = false

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
        if (idioma_padrao) {

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
                progressBarTemporada?.visibility = View.INVISIBLE
            }
        } else {
            recyclerViewTemporada.adapter = TemporadasAdapter(activity!!, series, onClickListener(), color, null)
            if (progressBarTemporada != null) {
                progressBarTemporada?.visibility = View.INVISIBLE
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

                val intent = Intent(context, TemporadaActivity::class.java)
                intent.putExtra(Constantes.NOME, getString(R.string.temporada) + " " + series.seasons?.get(position)?.seasonNumber)
                intent.putExtra(Constantes.TEMPORADA_ID, series.seasons?.get(position)?.seasonNumber)
                intent.putExtra(Constantes.TEMPORADA_POSITION, position)
                intent.putExtra(Constantes.TVSHOW_ID, series.id)
                intent.putExtra(Constantes.COLOR_TOP, color)
                context?.startActivity(intent)

            }

            override fun onClickCheckTemporada(view: View, position: Int) {

                if (isVisto(position)) {
                    Toast.makeText(context, R.string.marcado_nao_assistido_temporada, Toast.LENGTH_SHORT).show()
                    val user = if (mAuth?.currentUser != null) mAuth?.currentUser?.uid else ""
                    val id_serie = series.id.toString()
                    val childUpdates = HashMap<String, Any>()

                    childUpdates.put("/$user/seguindo/$id_serie/seasons/$position/visto", false)
                    setStatusEps(position, false)
                    userTvshow?.seasons?.get(position)?.userEps?.forEachIndexed { index, _ ->
                        childUpdates.put("/$user/seguindo/$id_serie/seasons/$position/userEps/$index/assistido", false)
                    }


                    myRef?.updateChildren(childUpdates)

                } else {
                    Toast.makeText(context, R.string.marcado_assistido_temporada, Toast.LENGTH_SHORT).show()
                    val user = if (mAuth?.currentUser != null) mAuth?.currentUser?.uid else ""
                    val id_serie = userTvshow?.id.toString()


                    val childUpdates = HashMap<String, Any>()
                    childUpdates.put("/$user/seguindo/$id_serie/seasons/$position/visto", true)
                    setStatusEps(position, true)
                    userTvshow?.seasons?.get(position)?.userEps?.forEachIndexed { index, _ ->
                        childUpdates.put("/$user/seguindo/$id_serie/seasons/$position/userEps/$index/assistido", true)
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
                progressBarTemporada?.visibility = View.VISIBLE
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
                                ?.child("seguindo")
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
                                    ?.child("seguindo")
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

        val animatorSet = AnimatorSet()
        val alphaStar = ObjectAnimator.ofFloat(img_star, "alpha", 0.0f, 1.0f)
                .setDuration(2000)
        val alphaMedia = ObjectAnimator.ofFloat(voto_media, "alpha", 0.0f, 1.0f)
                .setDuration(2300)
        val alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0.0f, 1.0f)
                .setDuration(3000)
        animatorSet.playTogether(alphaStar, alphaMedia, alphaSite)
        animatorSet.start()
    }

    private fun setPoster() {
        if (series.posterPath != null) {
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(requireContext(), 2))!! + series.posterPath!!)
                    .into(img_poster)

            img_poster?.setOnClickListener {
                val intent = Intent(context, PosterGridActivity::class.java)
                intent.putExtra(Constantes.POSTER, series.images?.posters as Serializable)
                intent.putExtra(Constantes.NOME, series.name)
                val transition = getString(R.string.poster_transition)
                val compat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(activity!!, img_poster, transition)
                ActivityCompat.startActivity(activity!!, intent, compat.toBundle())

            }
        } else {
            img_poster!!.setImageResource(R.drawable.poster_empty)
        }

        card_poster.setCardBackgroundColor(color)
    }

    private fun setProdutora() {
        if (series.productionCompanies!!.isNotEmpty()) {
            var primeiraProdutora = series.productionCompanies!![0]?.name
            if (primeiraProdutora?.length!! >= 27) {
                primeiraProdutora = primeiraProdutora.subSequence(0, 27) as String
                primeiraProdutora += "..." //Todo fazer no XML
            }
            produtora?.setTextColor(ContextCompat.getColor(context!!, R.color.primary))
            produtora?.text = primeiraProdutora

            produtora?.setOnClickListener {
                if (series.productionCompanies?.isNotEmpty()!!) {
                    val intent = Intent(context, ProdutoraActivity::class.java)
                    intent.putExtra(Constantes.PRODUTORA_ID, series.productionCompanies?.get(0)?.id)
                    context?.startActivity(intent)
                } else {
                    Toast.makeText(context, getString(R.string.sem_informacao_company), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setCategoria() {

        val genres = series.genres
        val stringBuilder = StringBuilder("")

        if (genres!!.isNotEmpty()) {
            for (genre in genres) {
                stringBuilder.append(" | " + genre?.name)

            }
        }
        categoria_tvshow?.text = stringBuilder.toString()
    }

    private fun setVotoMedia() {
        mediaNotas = getMedia()
        if (mediaNotas > 0) {
            img_star?.setImageResource(R.drawable.icon_star)
            val formatter = DecimalFormat("0.0")
            val valor = formatter.format(mediaNotas)
            voto_media?.text = valor
            frame_nota.contentDescription = valor

        } else {
            img_star?.setImageResource(R.drawable.icon_star_off)
            voto_media?.setText(R.string.valor_zero)
            voto_media?.setTextColor(ContextCompat.getColor(context!!, R.color.blue))
        }
    }

    private fun setTitulo() {
        if (series.name != null) {
            titulo_tvshow?.text = series.name
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

        if (series.originCountry!!.isNotEmpty()) {
            production_countries?.text = series.originCountry!![0]

        } else {
            production_countries?.text = getString(R.string.não_informado)
        }
    }

    private fun setPopularity() {

        val animatorCompat = ValueAnimator.ofFloat(1.0f, series.popularity!!.toFloat())
        if (series.popularity!! > 0) {

            animatorCompat.addUpdateListener { valueAnimator ->
                val valor = valueAnimator.animatedValue as Float
                var popularidade = valor.toString()

                if (popularidade[0] == '0' && isAdded) {
                    popularidade = popularidade.substring(2, popularidade.length)
                    popularity?.text = "$popularidade  ${getString(mil)}"

                } else {

                    val posicao = popularidade.indexOf(".") + 2
                    popularidade = popularidade.substring(0, posicao)
                    var milhoes: String? = null
                    if (isAdded) {
                        milhoes = getString(R.string.milhoes)
                    }
                    popularidade += (" " + milhoes)
                    popularity?.text = popularidade
                }

            }

            animatorCompat.duration = 900
            animatorCompat.setTarget(popularity)
            if (isAdded) {
                animatorCompat.start()
            }
        }
    }

    private fun setElenco() {

        textview_elenco?.setOnClickListener {
            val intent = Intent(context, ElencoActivity::class.java).apply {
                putExtra(Constantes.ELENCO, series.credits?.cast as Serializable)
                putExtra(Constantes.NOME, series.name)
            }
            startActivity(intent)
        }

        if (series.credits?.cast?.isNotEmpty()!!) {
            textview_elenco?.visibility = View.VISIBLE
            recycle_tvshow_elenco?.setHasFixedSize(true)
            recycle_tvshow_elenco?.itemAnimator = DefaultItemAnimator()
            recycle_tvshow_elenco?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recycle_tvshow_elenco.adapter =
                    CastAdapter(activity, series.credits?.cast)
        } else {
            textview_elenco.gone()
            recycle_tvshow_elenco.layoutParams.height = 1
        }
    }

    private fun setProducao() {

        textview_crews?.setOnClickListener {
            val intent = Intent(context, CrewsActivity::class.java).apply {
                putExtra(Constantes.PRODUCAO, series.credits?.crew as Serializable)
                putExtra(Constantes.NOME, series.name)
            }
            startActivity(intent)
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
            val intent = Intent(context, SimilaresActivity::class.java).apply {
                putExtra(Constantes.SIMILARES_TVSHOW, series.similar?.results as Serializable)
                putExtra(Constantes.NOME, series.name)
            }
            activity?.startActivity(intent)
        }

        if (series.similar?.results?.isNotEmpty()!!) {

            recycle_tvshow_similares?.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                adapter = SimilaresSerieAdapter(requireActivity(), series.similar?.results)
            }

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

            text_similares.visibility = View.VISIBLE

        } else {
            text_similares.gone()
            recycle_tvshow_similares.layoutParams.height = 1
        }
    }

    private fun setLancamento() {
        var inicio: String? = null
        if (series.firstAirDate != null) {
            inicio = series.firstAirDate?.subSequence(0, 4) as String
        }
        if (series.lastAirDate != null) {
            lancamento?.text = inicio + " - " + series.lastAirDate?.substring(0, 4)
        } else {
            lancamento?.text = inicio
        }
    }

    private fun setTrailer() {
        if (series.videos?.results?.isNotEmpty()!!) {
            recycle_tvshow_trailer.apply {
                recycle_tvshow_trailer?.setHasFixedSize(true)
                recycle_tvshow_trailer?.itemAnimator = DefaultItemAnimator()
                recycle_tvshow_trailer?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recycle_tvshow_trailer.adapter =
                        TrailerAdapter(activity, series.videos?.results, series.overview)
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

    fun getImdb(): Imdb? {

        val inscricaoImdb = Api(context!!).getOmdbpi(series.external_ids?.imdbId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Imdb> {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        if (context != null && view != null)
                            Toast.makeText(context, getString(R.string.ops), Toast.LENGTH_LONG).show()
                    }

                    override fun onNext(imdb: Imdb) {
                        imdbDd = imdb
                    }
                })

        subscriptions.add(inscricaoImdb)

        return null
    }

    private fun getMedia(): Float {

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



