package utils

import android.content.Context
import applicaton.BaseViewModel.*
import domain.CompanyFilmes
import domain.Credits
import domain.EpisodesItem
import domain.GuestSession
import domain.Imdb
import domain.ListaSeries
import domain.Movie
import domain.PersonPopular
import domain.TvSeasons
import domain.Videos
import domain.busca.MultiSearch
import domain.colecao.Colecao
import domain.movie.ListaFilmes
import domain.person.Person
import domain.reelgood.movie.ReelGoodMovie
import domain.reelgood.tvshow.ReelGoodTv
import domain.search.SearchMulti
import domain.tvshow.Tvshow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import rx.Observable
import utils.ApiSingleton.Companion.LoggingInterceptor
import utils.UtilsKt.Companion.getIdiomaEscolhido
import utils.key.ApiKeys
import java.io.IOException
import java.util.Locale
import java.util.Random
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
class Api(val context: Context) : ApiSingleton() {
	private var timeZone: String = getIdiomaEscolhido(context)
	private var region: String = Locale.getDefault().country
	private val baseUrl3 = "https://api.themoviedb.org/3/"
	private val baseUrl4 = "https://api.themoviedb.org/4/"
	val TMDBAPI = getKeyTMDB()
	val OMDBAPI = ApiKeys.OMDBAPI_API_KEY

	object TYPESEARCH {
		object MOVIE {
			const val popular: String = "popular"
			const val now: String = "now_playing"
			const val upComing: String = "upcoming"
			const val bestScore: String = "top_rated"
		}

		object TVSHOW {
			const val toDay: String = "airing_today"
			const val week: String = "on_the_air"
			const val popular: String = "popular"
			const val bestScore: String = "top_rated"
		}
	}

	private fun getKeyTMDB(): String {
		return if (Random(2).nextInt() % 2 == 0) ApiKeys.TMDB_API_KEY
		else ApiKeys.TMDB_API_KEY2
	}

	suspend fun personPopular(pagina: Int): BaseRequest<PersonPopular> {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}person/popular?page=$pagina&language=en-US&api_key=${TMDBAPI}",
				CallBackApiWithBaseRequest(cont, PersonPopular::class.java))
		}
	}

	suspend fun getListMovie(id: String, pagina: Int): BaseRequest<ListaFilmes> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl4}list/$id?page=$pagina&api_key=$TMDBAPI&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaFilmes::class.java))
		}
	}

	suspend fun getListMovieByType(type: String, page: Int): BaseRequest<ListaFilmes> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}movie/$type?api_key=$TMDBAPI&page=$page&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaFilmes::class.java))
		}
	}

	suspend fun getListTvByType(type: String, page: Int): BaseRequest<ListaSeries> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}tv/$type?api_key=${TMDBAPI}&page=$page&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaSeries::class.java))
		}
	}

	fun getCompanyFilmes(company_id: Int, pagina: Int = 1): Observable<CompanyFilmes> {
		return Observable.create { subscriber ->
			val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
			val request = Request.Builder()
				.url("${baseUrl3}company/$company_id/movies?page=$pagina&api_key=${TMDBAPI}&language=$timeZone")
				.get()
				.build()
			val response = client.newCall(request).execute()
			if (response.isSuccessful) {
				val json = response.body?.string()
				val companyFilmes = gson.fromJsonWithLog(json, CompanyFilmes::class.java)
				subscriber.onNext(companyFilmes)
				subscriber.onCompleted()
			} else {
				subscriber.onError(Throwable(response.message))
			}
		}
	}

	suspend fun getMovie(idMovie: Int): BaseRequest<Movie> {
		return suspendCancellableCoroutine { continuation ->
			val idioma = getIdiomaEscolhido(context)
			executeCall("${baseUrl3}movie/$idMovie?api_key=${TMDBAPI}&language=$idioma&append_to_response=credits,videos,images,release_dates,similar&include_image_language=en,null",
				CallBackApiWithBaseRequest(continuation, Movie::class.java))
		}
	}

	suspend fun getTvshow(idTvshow: Int): BaseRequest<Tvshow> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}tv/$idTvshow?api_key=${TMDBAPI}&language=$timeZone&append_to_response=credits,videos,images,release_dates,similar,external_ids&include_image_language=en,null",
				CallBackApiWithBaseRequest(continuation, Tvshow::class.java))
		}
	}

	suspend fun getTrailersFromEn(
		id: Int,
		type: String,
	): BaseRequest<Videos> {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}$type/$id/videos?api_key=${TMDBAPI}&language=en-US,null",
				CallBackApiWithBaseRequest(cont, Videos::class.java))
		}
	}

	suspend fun getTvShowLiteC(id: Int): Tvshow {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}tv/$id?api_key=$TMDBAPI&language=$timeZone&append_to_response=release_dates,external_ids&include_image_language=en,null",
				CallBackApi(cont, Tvshow::class.java))
		}
	}

	suspend fun getTvShowEpC(id: Int, idTemp: Int, idEp: Int): EpisodesItem {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}tv/$id/season/$idTemp/episode/$idEp?api_key=$TMDBAPI&language=$timeZone",
				CallBackApi(cont, EpisodesItem::class.java))
		}
	}

	suspend fun getCollection(id: Int): BaseRequest<Colecao> {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}collection/$id?api_key=${TMDBAPI}&language=$timeZone,en",
				CallBackApiWithBaseRequest(cont, Colecao::class.java))
		}
	}

	suspend fun getTvSeasons(id: Int, id_season: Int): TvSeasons {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}tv/$id/season/$id_season?api_key=${TMDBAPI}&language=$timeZone",
				CallBackApi(cont, TvSeasons::class.java))
		}
	}

	fun getTvCreditosTemporada(id: Int, id_season: Int): Observable<Credits> {
		return Observable.create { subscriber ->
			val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
			val request = Request.Builder()
				.url("${baseUrl3}tv/$id/season/$id_season/credits?api_key=${TMDBAPI}&language=en-US")
				.get()
				.build()
			val response = client.newCall(request).execute()
			if (response.isSuccessful) {
				val json = response.body?.string()
				val lista = gson.fromJsonWithLog(json, Credits::class.java)
				subscriber.onNext(lista)
				subscriber.onCompleted()
			} else {
				subscriber.onError(Throwable(response.message))
			}
		}
	}

	suspend fun personDetails(id: Int): BaseRequest<Person> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}person/$id?api_key=${TMDBAPI}&language=en-US&append_to_response=combined_credits,images,translations",
				CallBackApiWithBaseRequest(continuation, Person::class.java))
		}
	}

	fun procuraMulti(query: String?): Observable<MultiSearch> {
		return Observable
			.create { subscriber ->
				if (query.equals("search_suggest_query")) Throwable("")
				val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
				val request = Request.Builder()
					.url("${baseUrl3}search/multi?api_key=${TMDBAPI}&language=$timeZone&query=$query&page=1&include_adult=false")
					.get()
					.build()
				val response = client.newCall(request).execute()
				if (response.isSuccessful) {
					val json = response.body?.string()
					val multi = gson.fromJsonWithLog(json, MultiSearch::class.java)
					subscriber.onNext(multi)
					subscriber.onCompleted()
				} else {
					subscriber.onError(Throwable(response.message))
				}
			}
	}

	suspend fun getNowPlayingMovies(): BaseRequest<ListaFilmes> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}movie/now_playing?api_key=${TMDBAPI}&language=$timeZone&page=1&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaFilmes::class.java))
		}
	}

	suspend fun getMoviePopular(): BaseRequest<ListaFilmes> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("$baseUrl3/movie/popular?api_key=$TMDBAPI&language=$timeZone&page=1&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaFilmes::class.java))
		}
	}

	suspend fun getUpcoming(): BaseRequest<ListaFilmes> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}movie/upcoming?api_key=${TMDBAPI}&language=$timeZone&page=1&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaFilmes::class.java))
		}
	}

	suspend fun getAiringToday(): BaseRequest<ListaSeries> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}tv/airing_today?api_key=${TMDBAPI}&language=$timeZone&page=1&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaSeries::class.java))
		}
	}

	suspend fun getPopularTv(): BaseRequest<ListaSeries> {
		return suspendCancellableCoroutine { continuation ->
			executeCall("${baseUrl3}tv/popular?api_key=${TMDBAPI}&language=$timeZone&page=1&region=$region",
				CallBackApiWithBaseRequest(continuation, ListaSeries::class.java))
		}
	}

	suspend fun getAvaliableMovie(id: String): ReelGoodMovie {
		return suspendCancellableCoroutine { continuation ->
			executeCall("https://api.reelgood.com/v1/movie/$id?sources=amazon_prime%2Chbo%2Chulu_plus%2Cnetflix%2Cstarz%2Cgoogle_plus&free=false",
				CallBackApi(continuation, ReelGoodMovie::class.java))
		}
	}

	suspend fun getAvaliableShow(id: String): ReelGoodTv {
		return suspendCancellableCoroutine { continuation ->
			executeCall("https://api.reelgood.com/v1/show/$id?sources=amazon_prime%2Chbo%2Chulu_plus%2Cnetflix%2Cstarz%2Cgoogle_plus&free=false",
				CallBackApi(continuation, ReelGoodTv::class.java))
		}
	}

	suspend fun getTmdbSearch(query: String, page: Int = 1): SearchMulti {
		return suspendCancellableCoroutine { cont ->
			executeCall("${baseUrl3}search/multi?api_key=${TMDBAPI}&language=$timeZone&query=$query&page=$page&include_adult=false",
				CallBackApi(cont, SearchMulti::class.java))
		}
	}

	suspend fun userGuest(): BaseRequest<GuestSession> {
		return suspendCancellableCoroutine { cont ->
			executeCall("https://api.themoviedb.org/3/authentication/guest_session/new?api_key=${TMDBAPI}",
				CallBackApiWithBaseRequest(cont, GuestSession::class.java))
		}
	}

	suspend fun getResquestImdb(id: String): BaseRequest<Imdb> {
		return suspendCancellableCoroutine { cont ->
			executeCall("http://www.omdbapi.com/?i=$id&tomatoes=true&r=json&apikey=${OMDBAPI}",
				CallBackApiWithBaseRequest(cont, Imdb::class.java))
		}
	}

	suspend fun ratedMediaGuest(
		id: Int,
		rated: Float,
		guestSession: GuestSession,
		type: String = "movie",
	): Any {
		return suspendCancellableCoroutine { cont ->
			executePostCall(url = "https://api.themoviedb.org/3/$type/$id/rating?api_key=${TMDBAPI}&guest_session_id=${guestSession.guestSessionId}",
				postRequest = RequestBody.create(
					"application/json".toMediaTypeOrNull(),
					"{\"value\":$rated}"
				),
				func = object : Callback {
					override fun onFailure(call: Call, e: IOException) {
						cont.resumeWithException(e)
					}

					override fun onResponse(call: Call, response: Response) {
						cont.resume(Any())
					}
				})
		}
	}

	suspend fun ratedTvEpsodeeGuest(
		id: Int,
		seasonNumber: Int,
		episodeNumber: Int,
		rated: Float,
		idGuest: String,
	): Any {
		return suspendCancellableCoroutine { cont ->
			executePostCall(url = "${baseUrl3}tv/$id/season/$seasonNumber/episode/$episodeNumber/rating?api_key=${TMDBAPI}&guest_session_id=$idGuest",
				postRequest = RequestBody.create(
					"application/json".toMediaTypeOrNull(),
					"{\"value\":$rated}"
				),
				func = object : Callback {
					override fun onFailure(call: Call, e: IOException) {
						cont.resumeWithException(e)
					}

					override fun onResponse(call: Call, response: Response) {
						cont.resume(Any())
					}
				})
		}
	}
}