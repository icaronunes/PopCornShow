package tvshow.fragment

import Layout
import adapter.CastAdapter
import adapter.CrewAdapter
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import applicaton.BaseFragment
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string.*
import com.github.clans.fab.FloatingActionMenu
import configuracao.SettingsActivity
import domain.Imdb
import domain.tvshow.Tvshow
import elenco.ElencoActivity
import kotlinx.android.synthetic.main.poster_tvhsow_details_layout.card_poster
import kotlinx.android.synthetic.main.poster_tvhsow_details_layout.img_poster
import kotlinx.android.synthetic.main.tvshow_info.adView
import kotlinx.android.synthetic.main.tvshow_info.categoria_tvshow
import kotlinx.android.synthetic.main.tvshow_info.descricao
import kotlinx.android.synthetic.main.tvshow_info.frame_nota
import kotlinx.android.synthetic.main.tvshow_info.icon_site
import kotlinx.android.synthetic.main.tvshow_info.imdb_site
import kotlinx.android.synthetic.main.tvshow_info.img_star
import kotlinx.android.synthetic.main.tvshow_info.lancamento
import kotlinx.android.synthetic.main.tvshow_info.original_title
import kotlinx.android.synthetic.main.tvshow_info.popularity
import kotlinx.android.synthetic.main.tvshow_info.production_countries
import kotlinx.android.synthetic.main.tvshow_info.production_label
import kotlinx.android.synthetic.main.tvshow_info.produtora
import kotlinx.android.synthetic.main.tvshow_info.proximo_ep_date
import kotlinx.android.synthetic.main.tvshow_info.recycle_tvshow_elenco
import kotlinx.android.synthetic.main.tvshow_info.recycle_tvshow_producao
import kotlinx.android.synthetic.main.tvshow_info.recycle_tvshow_similares
import kotlinx.android.synthetic.main.tvshow_info.recycle_tvshow_trailer
import kotlinx.android.synthetic.main.tvshow_info.seguir
import kotlinx.android.synthetic.main.tvshow_info.status
import kotlinx.android.synthetic.main.tvshow_info.temporadas
import kotlinx.android.synthetic.main.tvshow_info.text_similares
import kotlinx.android.synthetic.main.tvshow_info.textview_crews
import kotlinx.android.synthetic.main.tvshow_info.textview_elenco
import kotlinx.android.synthetic.main.tvshow_info.titulo_tvshow
import kotlinx.android.synthetic.main.tvshow_info.tmdb_site
import kotlinx.android.synthetic.main.tvshow_info.ultimo_ep_name
import kotlinx.android.synthetic.main.tvshow_info.voto_media
import loading.firebase.TypeDataRef
import poster.PosterGridActivity
import producao.CrewsActivity
import produtora.activity.ProdutoraActivity
import similares.SimilaresActivity
import site.Site
import tvshow.adapter.SimilaresSerieAdapter
import tvshow.viewmodel.TvShowViewModel
import utils.Constant
import utils.Constant.BASEMOVIEDB_TV
import utils.Constant.IMDB
import utils.Constant.METACRITICTV
import utils.Constant.ROTTENTOMATOESTV
import utils.UtilsApp.setUserTvShow
import utils.gone
import utils.kotterknife.bindArgument
import utils.makeToast
import utils.minHeight
import utils.patternRecyler
import utils.removerAcentos
import utils.setPicasso
import utils.setScrollInvisibleFloatMenu
import utils.visible
import java.io.Serializable
import java.text.DecimalFormat
import java.util.Locale

/**
 * Created by icaro on 23/08/16.
 */
class TvShowFragment(override val layout: Int = Layout.tvshow_info) : BaseFragment() {
	private val model: TvShowViewModel by lazy { createViewModel(TvShowViewModel::class.java) }
	private val color: Int by bindArgument(Constant.COLOR_TOP)
	private lateinit var series: Tvshow
	private var mediaNotas: Float = 0.0f
	private var progressBarTemporada: ProgressBar? = null
	private var imdbDd: Imdb? = null

	companion object {
		@JvmStatic
		fun newInstance(color: Int): Fragment {
			return TvShowFragment().apply {
				arguments = Bundle().apply {
					putInt(Constant.COLOR_TOP, color)
				}
			}
		}
	}

	private fun setUpdateFromFire() {
		val childUpdates = java.util.HashMap<String, Any?>()
		childUpdates["nome"] = series.name
		childUpdates["numberOfEpisodes"] = series.numberOfEpisodes
		childUpdates["numberOfSeasons"] = series.numberOfSeasons
		childUpdates["poster"] = series.posterPath
		series.external_ids?.imdbId?.let { childUpdates["idImdb"] = it }
		model.update(series.id, childUpdates, TypeDataRef.FALLOW)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		observerTvshow()
		observerAuth()
		observerImdb()
	}

	private fun observerTvshow() {
		model.tvShow.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					series = it.result
					setAdMob(adView)
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
					setListeners()
					setNomeUltimoEp()
					setUltimoEpDate()
					setSinopse()
					updates()
					observerIsFallow()
				}
				is Failure -> ops()
				is Loading -> {
				}
			}
		})
	}

	private fun observerAuth() {
		model.auth.observe(viewLifecycleOwner, Observer {
			isSeguindo(it)
		})
	}

	private fun observerIsFallow() {
		model.isFallow.observe(viewLifecycleOwner, Observer {
			setFalow(it)
			if (it) setUpdateFromFire()
		})
	}

	private fun updates() {
		val childUpdates = java.util.HashMap<String, Any?>()
		childUpdates["title"] = series.name
		childUpdates["poster"] = series.posterPath
		series.external_ids?.imdbId?.let { childUpdates["idImdb"] = it }

		model.favorit.observe(viewLifecycleOwner, Observer {
			if (it.child("${series.id}").exists()) model.update(
				series.id,
				childUpdates,
				TypeDataRef.FAVORITY
			)
		})

		model.rated.observe(viewLifecycleOwner, Observer { dataSnapshot ->
			series.external_ids?.let { childUpdates["externalIds"] = it }
			if (dataSnapshot.child("${series.id}").exists()) model.update(
				series.id,
				childUpdates,
				TypeDataRef.RATED
			)
		})

		model.watch.observe(viewLifecycleOwner, Observer {
			if (it.child("${series.id}").exists()) model.update(
				series.id,
				childUpdates,
				TypeDataRef.WATCH
			)
		})
	}

	private fun observerImdb() {
		model.imdb.observe(viewLifecycleOwner, Observer {
			when (it) {
				is Success -> {
					imdbDd = it.result
					setVotoMedia()
				}
				is Failure -> requireActivity().makeToast(R.string.ops)
			}
		})
	}

	private fun getViewInformacoes(inflater: LayoutInflater?, container: ViewGroup?): View? {
		val view = inflater?.inflate(layout, container, false)
		progressBarTemporada = view?.findViewById(R.id.progress_temporadas)
		view?.findViewById<Button>(R.id.seguir)?.setOnClickListener { onClickSeguir() }
		return view
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = getViewInformacoes(inflater, container)

	private fun setUltimoEpDate() {
		proximo_ep_date.text = series.lastEpisodeAir?.airDate
	}

	private fun setNomeUltimoEp() {
		series.lastEpisodeAir?.let {
			ultimo_ep_name.text = it.name ?: ""
		}
	}

	private fun setFalow(fallow: Boolean) {
		if (fallow) {
			seguir?.setText(R.string.seguindo)
		} else {
			seguir?.setText(R.string.seguir)
		}
	}

	private fun setListeners() {
		icon_site?.setOnClickListener {
			if (!series.homepage.isNullOrBlank()) {
				startActivity(Intent(context, Site::class.java).apply {
					putExtra(Constant.SITE, series.homepage)
				})
			} else {
				snack(
					requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
					getString(R.string.no_site)
				)
			}
		}

		imdb_site?.setOnClickListener {
			series.external_ids?.imdbId?.let {
				startActivity(Intent(activity, Site::class.java).apply {
					putExtra(Constant.SITE, "${IMDB}$it")
				})
			}
		}

		tmdb_site?.setOnClickListener {
			startActivity(Intent(activity, Site::class.java).apply {
				putExtra(Constant.SITE, "${BASEMOVIEDB_TV}${series.id}")
			})
		}

		img_star?.setOnClickListener {
			clickStarsNote()
		}
	}

	@SuppressLint("InflateParams")
	private fun clickStarsNote() {
		if (mediaNotas > 0) {
			val builder = AlertDialog.Builder(requireActivity())
			val inflater = requireActivity().layoutInflater
			val layout = inflater.inflate(R.layout.layout_notas, null)

			imdbDd?.let { imdb ->
				imdb.imdbRating?.let {
					(layout
						?.findViewById<View>(R.id.nota_imdb) as TextView).text =
						String.format(getString(R.string.bar_ten), it)
				}

				imdb.metascore?.let {
					if (!it.contains("N/A"))
						(layout.findViewById<View>(R.id.nota_metacritic) as TextView).text =
							String.format(getString(R.string.bar_hundred), it)
				}

				imdb.tomatoRating?.let {
					if (!it.contains("N/A", true))
						(layout.findViewById<View>(R.id.nota_tomatoes) as TextView).text =
							String.format(getString(R.string.bar_ten), it)
				}
			}

			series.voteAverage?.let {
				if (!it.equals(0.0))
					(layout?.findViewById<View>(R.id.nota_tmdb) as TextView).text =
						String.format(getString(R.string.bar_ten), it)
			}

			layout.findViewById<View>(R.id.image_metacritic).setOnClickListener {
				imdbDd?.let {
					val nome = it.title.replace(" ", "-").toLowerCase(Locale.ROOT).removerAcentos()
					val url = "$METACRITICTV$nome"
					startActivity(Intent(activity, Site::class.java).apply {
						putExtra(Constant.SITE, url)
					})
				}
			}

			layout.findViewById<View>(R.id.image_tomatoes).setOnClickListener {
				imdbDd?.let {
					val nome = it.title.replace(" ", "_").toLowerCase(Locale.ROOT).removerAcentos()
					startActivity(Intent(activity, Site::class.java).apply {
						putExtra(Constant.SITE, "$ROTTENTOMATOESTV$nome")
					})
				}
			}

			layout.findViewById<View>(R.id.image_imdb).setOnClickListener {
				startActivity(Intent(activity, Site::class.java).apply {
					putExtra(Constant.SITE, "$IMDB${imdbDd?.imdbID}")
				})
			}

			layout.findViewById<View>(R.id.image_tmdb).setOnClickListener {
				startActivity(Intent(activity, Site::class.java).apply {
					putExtra(Constant.SITE, "$BASEMOVIEDB_TV${series.id!!}")
				})
			}

			builder.setView(layout)
			builder.show()
		} else {
			snack(
				requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
				getString(R.string.no_vote)
			)
		}
	}

	private fun isSeguindo(auth: Boolean) {
		if (!auth) setStatusButton()
	}

	private fun setStatus() {
		series.status?.let {
			status.setTextColor(color)
			val tradutor = PreferenceManager.getDefaultSharedPreferences(activity)
				.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
			if (tradutor) {
				when (it) {
					"Returning Series" -> status?.setText(R.string.returnin_series)
					"Ended" -> status!!.setText(R.string.ended)
					"Canceled" -> status?.setText(R.string.canceled)
					"In Production" -> status?.setText(in_production)
					else -> status?.text = it
				}
			} else {
				status?.text = it
			}
		}
	}

	private fun setStatusButton() {
		seguir?.isEnabled = false
		val tradutor = PreferenceManager.getDefaultSharedPreferences(activity)
			.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
		if (tradutor) {
			seguir?.text = resources.getText(R.string.sem_login)
		} else {
			seguir?.text = series.status
		}
	}

	private fun setTemporada() {
		if (series.numberOfSeasons!! > 0) {
			temporadas?.text = series.numberOfSeasons.toString()
		}
	}

	private fun onClickSeguir() {
		progressBarTemporada?.visible()
		model.setFallow(series.id,
			add = {
				it.child("${series.id}")
					.setValue(setUserTvShow(series))
					.addOnCompleteListener { task ->
						if (task.isSuccessful) {
							seguir?.setText(R.string.seguindo)
						} else {
							requireActivity().makeToast(R.string.erro_seguir)
						}
					}
			}, remove = {
				AlertDialog.Builder(requireContext())
					.setTitle(R.string.title_delete)
					.setMessage(R.string.msg_parar_seguir)
					.setNegativeButton(R.string.no, null)
					.setOnDismissListener { progressBarTemporada?.visibility = View.GONE }
					.setPositiveButton(R.string.ok) { _, _ ->
						it.child("${series.id}")
							.removeValue()
							.addOnCompleteListener { task ->
								if (task.isSuccessful) {
									seguir?.setText(R.string.seguir)
								} else {
									requireActivity().makeToast(R.string.erro_seguir)
								}
							}
						progressBarTemporada?.visibility = View.GONE
					}.create().show()
			})
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
		img_poster?.setPicasso(series.posterPath, 4, img_erro = R.drawable.poster_empty)
		val posters = series.images?.posters

		img_poster?.setOnClickListener {
			if (posters != null && posters.isNotEmpty()) {
				val intent = Intent(context, PosterGridActivity::class.java).apply {
					putExtra(Constant.POSTER, posters as Serializable)
					putExtra(Constant.NAME, series.name)
				}
				val compat = ActivityOptionsCompat
					.makeSceneTransitionAnimation(
						requireActivity(),
						img_poster,
						getString(R.string.poster_transition)
					)
				ActivityCompat.startActivity(requireActivity(), intent, compat.toBundle())
			} else {
				requireActivity().makeToast(R.string.poster_empty)
			}
		}

		card_poster.setCardBackgroundColor(color)
	}

	private fun setProdutora() {
		series.productionCompanies?.let {
			val production = series.productionCompanies?.getOrNull(0)
			produtora?.setTextColor(color)
			if (production?.name.isNullOrBlank()) {
				produtora.gone()
				production_label.gone()
			} else {
				produtora?.text = production?.name
			}

			produtora?.setOnClickListener {
				if (production != null) {
					context?.startActivity(Intent(context, ProdutoraActivity::class.java).apply {
						putExtra(Constant.PRODUTORA_ID, production.id)
					})
				} else {
					Toast.makeText(
						context,
						getString(R.string.sem_informacao_company),
						Toast.LENGTH_SHORT
					).show()
				}
			}
		}
	}

	private fun setCategoria() {
		categoria_tvshow?.text = StringBuilder().apply {
			series.genres?.forEachIndexed { index, it ->
				if (index != 0) append(" | ")
				append("${it?.name}")
			}
		}.toString()
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
			voto_media?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
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
		// Todo refazer metodo
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
			startActivity(Intent(requireContext(), ElencoActivity::class.java).apply {
				putExtra(Constant.ELENCO, series.credits?.cast as Serializable)
				putExtra(Constant.NAME, series.name)
			})
		}

		if (series.credits?.cast?.isNotEmpty()!!) {
			textview_elenco?.visible()
			recycle_tvshow_elenco.patternRecyler(true).apply {
				adapter = CastAdapter(requireActivity(), series.credits?.cast ?: listOf())
				setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
			}
		} else {
			textview_elenco.gone()
			recycle_tvshow_elenco.apply {
				layoutParams.height = 1
				importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
				isFocusable = false
			}
		}
	}

	private fun setProducao() {
		textview_crews?.setOnClickListener {
			startActivity(Intent(requireContext(), CrewsActivity::class.java).apply {
				putExtra(Constant.PRODUCAO, series.credits?.crew as Serializable)
				putExtra(Constant.NAME, series.name)
			})
		}

		if (series.credits?.crew?.isNotEmpty()!!) {
			textview_crews?.visibility = View.VISIBLE
			recycle_tvshow_producao.patternRecyler(true).apply {
				adapter = CrewAdapter(requireActivity(), series.credits?.crew ?: listOf())
				setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
			}
		} else {
			textview_crews.gone()
			recycle_tvshow_producao.minHeight()
		}
	}

	private fun setSimilares() {
		text_similares.setOnClickListener {
			startActivity(Intent(requireContext(), SimilaresActivity::class.java).apply {
				putExtra(Constant.SIMILARES_TVSHOW, series.similar?.results as Serializable)
				putExtra(Constant.NAME, series.name)
			})
		}

		if (series.similar?.results?.isNotEmpty()!!) {
			recycle_tvshow_similares.patternRecyler().apply {
				adapter = SimilaresSerieAdapter(requireActivity(), series.similar?.results)
				setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
			}
			text_similares.visible()
		} else {
			text_similares.gone()
			recycle_tvshow_similares.minHeight()
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
		if (series.videos?.results?.isNotEmpty() == true) {
			recycle_tvshow_trailer.apply {
				itemAnimator = DefaultItemAnimator()
				layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
				adapter = TrailerAdapter(series.videos?.results, series.overview ?: "")
				setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
			}
		} else {
			recycle_tvshow_trailer.minHeight()
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

	private fun getMedia(): Float {
		// Todo - refazer metodo
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
