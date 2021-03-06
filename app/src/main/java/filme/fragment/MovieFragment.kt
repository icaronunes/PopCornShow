package filme.fragment

import Layout
import adapter.TrailerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import domain.Imdb
import domain.Movie
import domain.colecao.PartsItem
import elenco.WorksActivity
import elenco.adapter.WorksAdapter
import filme.MovieDetatilsViewModel
import filme.adapter.CollectionPagerAdapter
import filme.adapter.SimilaresFilmesAdapter
import ifValid
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
import produtora.activity.ProductionActivity
import similares.SimilaresActivity
import site.Site
import utils.Constant
import utils.Constant.BASEMOVIEDB_MOVIE
import utils.Constant.IMDB
import utils.Constant.METACRITICMOVIE
import utils.Constant.ROTTENTOMATOESMOVIE
import utils.enums.EnumTypeMedia
import utils.gone
import utils.kotterknife.bindArgument
import utils.makeToast
import utils.minHeight
import utils.parseDate
import utils.patternRecyler
import utils.putString
import utils.removerAcentos
import utils.resolver
import utils.setPicasso
import utils.setScrollInvisibleFloatMenu
import utils.visible
import java.io.Serializable
import java.text.DecimalFormat
import java.util.Locale

/**
 * Created by icaro on 03/07/16.
 */
class MovieFragment(override val layout: Int = Layout.movie_details_info) : BaseFragment() {
	private lateinit var movieDb: Movie
	private lateinit var imdbDd: Imdb
	private val model: MovieDetatilsViewModel by lazy { createViewModel(MovieDetatilsViewModel::class.java) }
	private val color: Int by bindArgument(Constant.COLOR_TOP, 0)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observers()
		setAdMob(adView)

		imdb_site.setOnClickListener {
			startActivity(Intent(requireActivity(), Site::class.java).apply {
				putExtra(Constant.SITE, "${IMDB}${movieDb.imdbId}/")
			})
		}

		tmdb_site.setOnClickListener {
			startActivity(Intent(requireActivity(), Site::class.java).apply {
				putExtra(Constant.SITE, "${BASEMOVIEDB_MOVIE}${movieDb.id}/")
			})
		}

		img_budget.setOnClickListener {
			movieDb.budget?.let {
				if (it > 0) {
					var valor = it.toString()
					if (valor.length >= 6)
						valor = valor.substring(0, valor.length - 6)
					snack(
						requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
						"${getString(R.string.orcamento_budget)} $$valor ${getString(R.string.milhoes_budget)}"
					)
				} else {
					snack(
						requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
						R.string.no_budget
					)
				}
			}
		}

		icon_site.setOnClickListener { openSite() }
		img_star.setOnClickListener(onClickImageStar())
		icon_collection.setOnClickListener {
			if (movieDb.belongsToCollection != null) {
				movieDb.belongsToCollection?.id?.let { it1 -> model.fetchCollection(it1) }
				it.contentDescription =
					R.string.sem_informacao_colletion.putString(requireContext())
			} else {
				snack(
					requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
					R.string.sem_informacao_colletion
				)
			}
		}

		textview_elenco.setOnClickListener {
			startActivity(Intent(requireActivity(), WorksActivity::class.java).apply {
				putExtra(Constant.NAME, movieDb.title)
				putExtra(Constant.ID, movieDb.id)
				putExtra(Constant.MEDIATYPE, EnumTypeMedia.MOVIE)
				putExtra(Constant.WORK, Constant.ViewTypesIds.CAST)
			})
		}

		textview_crews.setOnClickListener {
			startActivity(Intent(requireActivity(), WorksActivity::class.java).apply {
				putExtra(Constant.NAME, movieDb.title)
				putExtra(Constant.ID, movieDb.id)
				putExtra(Constant.MEDIATYPE, EnumTypeMedia.MOVIE)
				putExtra(Constant.WORK, Constant.ViewTypesIds.CREWS)
			})
		}

		textview_similares.setOnClickListener {
			val intent = Intent(requireActivity(), SimilaresActivity::class.java).apply {
				putExtra(Constant.MEDIATYPE, Constant.MOVIE)
				putExtra(Constant.SIMILARES, movieDb.similar?.resultsSimilar as Serializable)
				putExtra(Constant.NAME, movieDb.title)
			}
			startActivity(intent)
		}
	}

	private fun openSite() {
		movieDb.homepage.let {
			if (it.isNullOrBlank()) {
				snack(
					requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
					Txt.no_site
				)
			} else {
				startActivity(Intent(requireActivity(), Site::class.java).apply {
					putExtra(Constant.SITE, it)
				})
			}
		}
	}

	private fun getImdbData() {
		::movieDb.isInitialized.ifValid {
			movieDb.imdbId?.let {
				model.getImdb(it)
			}
		}
	}

	private fun observers() {
		model.videos.observe(viewLifecycleOwner, Observer {
			it.resolver(requireActivity(), { videos ->
				if (videos.results.isNullOrEmpty()) {
					recycle_filme_trailer.minHeight()
				} else {
					recycle_filme_trailer.patternRecyler().apply {
						adapter = TrailerAdapter(videos.results, movieDb.overview ?: "")
					}
				}
			}, {
				recycle_filme_trailer.minHeight()
			})
		})

		model.imdb.observe(viewLifecycleOwner, Observer {
			it.resolver(requireActivity(), { imdb ->
				imdbDd = imdb
				setVotoMedia()
			}, {
				voto_media.gone()
				img_star.gone()
				makeToast(R.string.ops)
			})
		})

		model.collection.observe(viewLifecycleOwner, Observer {
			it.resolver(requireActivity(), { collections ->
				getCollection(collections.parts?.sortedBy { part -> part?.releaseDate })
			}, {
				makeToast(R.string.ops)
			})
		})

		model.movie.observe(viewLifecycleOwner, Observer { it ->
			it.resolver(requireActivity(), { movie ->
				movieDb = movie
				getImdbData()
				setTitulo()
				setGenres()
				setRelease()
				setTimeFilme()
				setProducts()
				setSinopse()
				setPoster()
				setBuget()
				setHome()
				setOriginalTitle()
				setSpokenLanguages()
				setProductionCountries()
				setPopularity()
				setCollection()
				setCast()
				setCrews()
				setTrailer()
				setSimilares()
				setAnimated()
				setStatus()
			}, {
				makeToast(R.string.ops)
			})
		})
	}

	private fun setStatus() {
		movieDb.status?.let {
			status.text = it
			status.setTextColor(color)
		}
	}

	@SuppressLint("InflateParams")
	private fun onClickImageStar(): View.OnClickListener? {
		return View.OnClickListener {
			if (mediaNotas > 0) {
				val layout = layoutInflater.inflate(R.layout.layout_notas, null)
				::imdbDd.isInitialized.ifValid {
					fillDialogRateds(layout)
					layout.findViewById<ImageView>(R.id.image_metacritic)
						.setOnClickListener {
							imdbDd.let {
								it.title?.let { title ->
									val clenaName =
										title.replace(" ", "-").toLowerCase(Locale.ROOT)
											.removerAcentos()
									val url = "$METACRITICMOVIE$clenaName"
									startActivity(Intent(requireActivity(),
										Site::class.java).apply {
										putExtra(Constant.SITE, url)
									})
								}
							}
						}

					layout.findViewById<ImageView>(R.id.image_tomatoes)
						.setOnClickListener {
							imdbDd.let {
								it.title?.let { title ->
									val cleanName =
										title.replace(" ", "_").toLowerCase(Locale.ROOT)
											.removerAcentos()
									startActivity(Intent(requireActivity(),
										Site::class.java).apply {
										putExtra(Constant.SITE, "$ROTTENTOMATOESMOVIE$cleanName")
									})
								}
							}
						}

					layout.findViewById<ImageView>(R.id.image_imdb)
						.setOnClickListener OnClickListener@{
							startActivity(Intent(requireActivity(), Site::class.java).apply {
								putExtra(Constant.SITE, "$IMDB${imdbDd.imdbID}")
							})
						}
				}
				layout.findViewById<ImageView>(R.id.image_tmdb)
					.setOnClickListener {
						val url = "$BASEMOVIEDB_MOVIE${movieDb.id}"
						startActivity(Intent(requireActivity(), Site::class.java).apply {
							putExtra(Constant.SITE, url)
						})
					}
				val builder = Builder(requireActivity())
				builder.setView(layout)
				builder.show()
			} else {
				snack(
					requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu),
					getString(R.string.no_vote)
				)
			}
		}
	}

	private fun fillDialogRateds(layout: View) {
		if(!::movieDb.isInitialized) return
		imdbDd.imdbRating?.let {
			layout.findViewById<TextView>(R.id.nota_imdb)
				.text = String.format(getString(Txt.bar_ten), it)
		}

		imdbDd.tomatoRating?.let {
			layout.findViewById<TextView>(R.id.nota_tomatoes)
				.text = String.format(getString(Txt.bar_ten), it)
		}

		imdbDd.imdbRating?.let {
			layout.findViewById<TextView>(R.id.nota_imdb)
				.text = String.format(getString(Txt.bar_ten), it)
		}

		imdbDd.metascore?.let {
			layout.findViewById<TextView>(R.id.nota_metacritic)
				.text = String.format(getString(Txt.bar_hundred), it)
		}

		movieDb.voteAverage?.let {
			layout.findViewById<TextView>(R.id.nota_tmdb)
				.text = String.format(getString(Txt.bar_ten), it)
		}
	}

	@SuppressLint("InflateParams")
	private fun getCollection(colecao: List<PartsItem?>?) {
		if (colecao?.isNullOrEmpty() == true) {
			makeToast(R.string.sem_informacao_colletion)
		} else {
			openDialogCollection(colecao)
		}
	}

	@SuppressLint("InflateParams")
	private fun openDialogCollection(collection: List<PartsItem?>?) {
		Builder(requireActivity()).apply {
			val dialogCollection =
				layoutInflater.inflate(Layout.dialog_collection, null)
			setView(dialogCollection).apply {
				val pager = dialogCollection.findViewById<ViewPager>(R.id.viewpager_collection)
				pager?.adapter = CollectionPagerAdapter(collection, requireActivity())
			}
			show()
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
			val drawable = if (it > 0) R.drawable.orcamento else R.drawable.sem_orcamento
			img_budget.setImageResource(drawable)
		}
	}

	@SuppressLint("Recycle")
	private fun setAnimated() {
		AnimatorSet().apply {
			playTogether(
				ObjectAnimator.ofFloat(img_star, View.ALPHA, 0.0f, 1.0f).setDuration(2000),
				ObjectAnimator.ofFloat(voto_media, View.ALPHA, 0f, 1f).setDuration(2300),
				ObjectAnimator.ofFloat(img_budget, View.ALPHA, 0f, 1f).setDuration(2500),
				ObjectAnimator.ofFloat(icon_site, View.ALPHA, 0f, 1f).setDuration(3000),
				ObjectAnimator.ofFloat(icon_collection, View.ALPHA, 0f, 1f).setDuration(3300)
			)
		}.start()
	}

	private fun setPoster() {
		img_poster.setPicasso(movieDb.posterPath, 3, img_erro = R.drawable.poster_empty)
			.setOnClickListener {
				val posters = movieDb.images?.posters
				if (posters != null && posters.isNotEmpty()) {
					val intent = Intent(requireActivity(), PosterGridActivity::class.java).apply {
						putExtra(Constant.POSTER, posters as Serializable)
						putExtra(Constant.NAME, movieDb.title)
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

	private fun setProducts() {
		movieDb.productionCompanies?.let {
			val production = movieDb.productionCompanies?.getOrNull(0)
			produtora.run {
				text = production?.name
				setTextColor(color)
				setOnClickListener {
					production?.let {
						startActivity(
							Intent(
								requireActivity(),
								ProductionActivity::class.java
							).apply {
								putExtra(Constant.ID, production.id)
								putExtra(Constant.ENDERECO, production.logoPath ?: "")
								putExtra(Constant.NAME, production.name)
							})
					}
				}
			}
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

	private fun setGenres() {
		categoria_filme.text = StringBuilder("").apply {
			movieDb.genres?.forEachIndexed { index, genero ->
				if (index != 0) append(" | ")
				append(genero?.name)
			}
		}.toString()
	}

	private fun setTitulo() {
		movieDb.title?.let { titulo_text.text = it }
	}

	private fun setTimeFilme() {
		if (movieDb.runtime != null) {
			makeTimeMovie()
		} else {
			time_filme?.text = getString(R.string.tempo_nao_informado)
		}
	}

	private fun makeTimeMovie() {
		val ONE_HOUR = 60
		val runTime = movieDb.runtime!!
		val hour = runTime / ONE_HOUR
		val min = runTime % ONE_HOUR
		val stringHours = "${hour}${getString(if (hour > 1) Txt.horas else Txt.hora)}"
		val stringMin = "$min ${getString(Txt.minutos)}"
		time_filme?.text = "$stringHours $stringMin"
	}

	private fun setOriginalTitle() {
		if (movieDb.originalTitle != null) {
			original_title.text = movieDb.originalTitle
		} else {
			original_title.text = getString(R.string.original_title)
		}
	}

	private fun setSpokenLanguages() {
		movieDb.spokenLanguages?.let {
			if (it.isNullOrEmpty()) {
				spoken_languages.text = getString(R.string.nao_informado)
			} else {
				spoken_languages.text = it[0]?.name
			}
		}
	}

	private fun setProductionCountries() {
		movieDb.productionCountries?.let {
			if (it.isNullOrEmpty()) {
				production_countries.text = getString(R.string.nao_informado)
			} else {
				production_countries.text = movieDb.productionCountries!![0]?.name
			}
		}
	}

	private fun setPopularity() {
		if (movieDb.popularity!! > 0) {
			ValueAnimator.ofFloat(1.0f, movieDb.popularity!!.toFloat()).apply {
				duration = 900
				setTarget(popularity)
				if (isAdded) start()
			}.addUpdateListener { valueAnimator ->
				var popValue = valueAnimator.animatedValue.toString()

				if (popValue[0] == '0' && isAdded) {
					popValue = popValue.substring(2, popValue.length)
					popularity.text = getString(R.string.mil, popValue)
				} else {
					val position = popValue.indexOf(".") + 2
					popValue = popValue.substring(0, position)

					if (isAdded) {
						popValue += " ${getString(R.string.milhoes)}"
						popularity.text = popValue
					}
				}
			}
		}
	}

	private fun setCast() {
		if (isAdded)
			if (movieDb.credits?.cast.isNullOrEmpty()) {
				textview_elenco?.gone()
				recycle_filme_elenco.minHeight()
			} else {
				recycle_filme_elenco.patternRecyler().apply {
					adapter = WorksAdapter(requireActivity(), movieDb.credits?.cast ?: listOf())
					setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
				}
				textview_elenco?.visible()
			}
	}

	private fun setCrews() {
		if (movieDb.credits?.crew!!.isNotEmpty() && isAdded) {
			recycle_filme_producao.patternRecyler().apply {
				adapter = WorksAdapter(requireActivity(), movieDb.credits?.crew ?: listOf())
				setScrollInvisibleFloatMenu(requireActivity().findViewById<FloatingActionMenu>(R.id.fab_menu))
			}
			textview_crews.visible()
		} else {
			textview_crews.gone()
			recycle_filme_producao.minHeight()
		}
	}

	private fun setTrailer() {
		if (movieDb.videos?.results?.isNullOrEmpty() == true) {
			hasTrailer()
		} else {
			recycle_filme_trailer?.patternRecyler()?.apply {
				adapter = TrailerAdapter(
					movieDb.videos?.results
						?: mutableListOf(),
					movieDb.overview
						?: ""
				)
				setScrollInvisibleFloatMenu(requireActivity().findViewById(R.id.fab_menu))
			}
		}
	}

	private fun hasTrailer() {
		model.getTrailerEn(idMovie = movieDb.id)
	}

	private fun setSimilares() {
		if (movieDb.similar?.resultsSimilar?.isNullOrEmpty() == false) {
			recycle_filme_similares?.apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager =
					LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
				adapter = SimilaresFilmesAdapter(requireActivity(),
					movieDb.similar?.resultsSimilar ?: listOf())
			}

			recycle_filme_similares.setScrollInvisibleFloatMenu(
				requireActivity().findViewById<FloatingActionMenu>(
					R.id.fab_menu
				)
			)
			textview_similares.visible()
		} else {
			textview_similares.gone()
			recycle_filme_similares.minHeight()
		}
	}

	private fun setRelease() {
		if (movieDb.releaseDates?.resultsReleaseDates?.isNotEmpty() == true) {
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

	private fun setCollection() {
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

			::movieDb.isInitialized.ifValid {
				movieDb.voteAverage?.let {
					try {
						tmdb = it
						tamanho++
					} catch (e: Exception) {
					}
				}
			}

			if (::imdbDd.isInitialized) {
				if (imdbDd.imdbRating != null) {
					if (!imdbDd.imdbRating!!.isEmpty()) {
						try {
							imdbDd.let {
								imdb = java.lang.Float.parseFloat(it.imdbRating)
								tamanho++
							}
						} catch (e: Exception) {
						}
					}
				}

				if (imdbDd.metascore != null) {
					if (!imdbDd.metascore!!.isEmpty()) {
						try {
							imdbDd.let {
								val meta = java.lang.Float.parseFloat(it.metascore)
								val nota = meta / 10
								metascore = nota
								tamanho++
							}
						} catch (e: Exception) {
						}
					}
				}

				if (imdbDd.tomatoRating != null) {
					if (!imdbDd.tomatoRating!!.isEmpty()) {
						try {
							imdbDd.let {
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
}
