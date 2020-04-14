package utils

import android.content.Context
import applicaton.BaseViewModel.BaseRequest
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Success
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import domain.Company
import domain.CompanyFilmes
import domain.Credits
import domain.EpisodesItem
import domain.GuestSession
import domain.Imdb
import domain.ListaSeries
import domain.Movie
import domain.MovieDb
import domain.PersonPopular
import domain.ReviewsUflixit
import domain.TvSeasons
import domain.Videos
import domain.busca.MultiSearch
import domain.colecao.Colecao
import domain.movie.ListaFilmes
import domain.person.Person
import domain.reelgood.movie.ReelGood
import domain.reelgood.tvshow.ReelGoodTv
import domain.search.SearchMulti
import domain.tvshow.Tvshow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import rx.Observable
import utils.Api.TYPESEARCH.FILME
import utils.Api.TYPESEARCH.SERIE
import utils.ApiSingleton.Companion.LoggingInterceptor
import utils.UtilsKt.Companion.getIdiomaEscolhido
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale
import java.util.Random
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Api(val context: Context) : ApiSingleton() {

    private var timeZone: String = getIdiomaEscolhido(context)
    private var region: String = Locale.getDefault().country
    private val baseUrl3 = "https://api.themoviedb.org/3/"
    private val baseUrl4 = "https://api.themoviedb.org/4/"

    object TYPESEARCH {

        object FILME {
            val popular: String = "popular"
            val now: String = "now_playing"
            val upComing: String = "upcoming"
            val bestScore: String = "top_rated"
        }

        object SERIE {
            val toDay: String = "airing_today"
            val week: String = "on_the_air"
            val popular: String = "popular"
            val bestScore: String = "top_rated"
        }
    }

    private fun getKey(): String {
        return if (Random(2).nextInt() % 2 == 0) Config.TMDB_API_KEY else Config.TMDB_API_KEY2
    }

    fun personPopular(pagina: Int): Observable<PersonPopular> {
        return Observable.create { subscriber ->
            executeCall("${baseUrl3}person/popular?page=$pagina&language=en-US&api_key=${Config.TMDB_API_KEY}",
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        subscriber.onError(Throwable(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val person = gson.fromJson(json, PersonPopular::class.java)
                            subscriber.onNext(person)
                            subscriber.onCompleted()
                        } else {
                            subscriber.onError(Throwable(response.message))
                        }
                    }
                })
        }
    }

    fun getLista(id: String, pagina: Int = 1): Observable<ListaFilmes> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl4}list/" + id + "?page=" + pagina + "&api_key=" + Config.TMDB_API_KEY)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = gson.fromJson(json, ListaFilmes::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getOmdbpi(id: String?): Observable<Imdb> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("http://www.omdbapi.com/?i=$id&tomatoes=true&r=json&apikey=${Config.OMDBAPI_API_KEY}") // Api de alguem)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val imdb = gson.fromJson(json, Imdb::class.java)
                subscriber.onNext(imdb)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getCompany(id_produtora: Int): Observable<Company> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}company/$id_produtora?api_key=" + Config.TMDB_API_KEY)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val company = gson.fromJson(json, Company::class.java)
                subscriber.onNext(company)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getCompanyFilmes(company_id: Int, pagina: Int = 1): Observable<CompanyFilmes> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}company/$company_id/movies?page=$pagina&api_key=${Config.TMDB_API_KEY}&language=$timeZone")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val companyFilmes = gson.fromJson(json, CompanyFilmes::class.java)
                subscriber.onNext(companyFilmes)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun buscaDeFilmes(tipoDeBusca: String? = FILME.now, pagina: Int = 1, local: String = "US"): Observable<ListaFilmes> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val url = "${baseUrl3}movie/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=$region"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = Gson().fromJson(json, ListaFilmes::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun buscaDeSeries(tipoDeBusca: String? = SERIE.popular, pagina: Int = 1, local: String = "US"): Observable<ListaSeries> {
        // Todo Erro na busca da paginacao
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = Gson().fromJson(json, ListaSeries::class.java)
                lista.results
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    suspend fun getMovie(idMovie: Int): BaseRequest<Movie> {
        return suspendCancellableCoroutine { continuation ->
            val idioma = getIdiomaEscolhido(context)
            executeCall("${baseUrl3}movie/$idMovie?api_key=${Config.TMDB_API_KEY}&language=$idioma&append_to_response=credits,videos,images,release_dates,similar&include_image_language=en,null",
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Failure(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            if (response.isSuccessful) {
                                val listMovie = Gson().fromJson(response.body?.string(),
                                    Movie::class.java)
                                continuation.resume(Success(listMovie))
                            } else {
                                continuation.resume(Failure(Exception(response.message)))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Failure(e))
                        }
                    }
                })
        }
    }

    suspend fun getTrailersFromEn(movieId: Int): BaseRequest<Videos> { // Todo Validar erros
        return suspendCancellableCoroutine { cont ->
            executeCall("${baseUrl3}movie/$movieId/videos?api_key=${Config.TMDB_API_KEY}&language=en-US,null", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resume(Failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val videos = gson.fromJson(json, Videos::class.java)
                            cont.resume(Success(videos))
                        } else {
                            cont.resume(Failure(java.lang.Exception(response.code.toString())))
                        }
                    } catch (ex: JsonSyntaxException) {
                        cont.resume(Failure(ex))
                    } catch (ex: SocketTimeoutException) {
                        cont.resume(Failure(ex))
                    } catch (ex: Exception) {
                        cont.resume(Failure(ex))
                    }
                }
            })
        }
    }

    fun getTvshowVideos(id: Int): Observable<Videos> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/videos?api_key=${Config.TMDB_API_KEY}&language=en-US,null")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val videos = gson.fromJson(json, Videos::class.java)

                subscriber.onNext(videos)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getTvShow(id: Int): Observable<Tvshow> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                    "&append_to_response=credits,videos,images,release_dates,similar,external_ids&include_image_language=en,null")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val tvshow = gson.fromJson(json, Tvshow::class.java)
                subscriber.onNext(tvshow)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getTvShowLite(id: Int): Observable<Tvshow> { // Usado em "Seguindo"
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                    "&append_to_response=release_dates,external_ids&include_image_language=en,null")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val tvshow = gson.fromJson(json, Tvshow::class.java)
                subscriber.onNext(tvshow)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    suspend fun getTvShowLiteC(id: Int): Tvshow { // Usado em "Seguindo"
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                    "&append_to_response=release_dates,external_ids&include_image_language=en,null")
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val tvshow = Gson().fromJson(json, Tvshow::class.java)
                            cont.resume(tvshow)
                        } else {
                            cont.cancel(null)
                        }
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }
            })
        }
    }

    suspend fun getTvShowEpC(id: Int, idTemp: Int, idEp: Int): EpisodesItem { // Usado em "Seguindo"
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/season/$idTemp/episode/$idEp?api_key=${getKey()}" + "&language=$timeZone")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val ep = Gson().fromJson(json, EpisodesItem::class.java)
                            cont.resume(ep)
                        } else {
                            cont.cancel(null)
                        }
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }
            })
        }
    }

    fun getColecao(id: Int): Observable<Colecao> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}collection/$id?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val colecao = gson.fromJson(json, Colecao::class.java)

                subscriber.onNext(colecao)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

//    fun loadMovieComVideo(id: Int): Observable<Movie> {
//        return getMovie(id)
//            .flatMap { it ->
//                Observable.just(it)
//                    .flatMap { video -> Observable.just(video.videos) }
//                    .flatMap { videos ->
//                        if (videos?.results?.isEmpty()!!) {
//                            Observable.zip(Observable.just(it), getMovieVideos(id)
//                                .flatMap { video ->
//                                    if (video.results?.isNotEmpty()!!) {
//                                        it.videos?.results?.addAll(video.results)
//                                        Observable.from(video.results)
//                                    } else {
//                                        Observable.just(it)
//                                    }
//                                }
//                            ) { movie, _ ->
//                                movie
//                            }
//                        } else {
//                            Observable.just(it)
//                        }
//                    }
//            }
//    }

    fun loadTvshowComVideo(id: Int): Observable<Tvshow> {
        return getTvShow(id)
            .flatMap { it ->
                Observable.just(it)
                    .flatMap { video -> Observable.just(video.videos) }
                    .flatMap { videos ->
                        if (videos?.results?.isEmpty()!!) {
                            Observable.zip(
                                Observable.just(it),
                                getTvshowVideos(id)
                                    .flatMap { video ->
                                        if (video.results?.isNotEmpty()!!) {
                                            it.videos?.results?.addAll(video.results)
                                            Observable.from(video.results)
                                        } else {
                                            Observable.just(it)
                                        }
                                    }
                            ) { movie, _ ->
                                movie
                            }
                        } else {
                            Observable.just(it)
                        }
                    }
            }
    }

    fun getTvSeasons(id: Int, id_season: Int, pagina: Int = 1): Observable<TvSeasons> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/season/$id_season?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = gson.fromJson(json, TvSeasons::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    suspend fun getTvSeasons(id: Int, id_season: Int): TvSeasons {
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/season/$id_season?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val tvshow = Gson().fromJson(json, TvSeasons::class.java)
                            cont.resume(tvshow)
                        } else {
                            cont.cancel(null)
                        }
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }
            })
        }
    }

    fun getTvCreditosTemporada(id: Int, id_season: Int): Observable<Credits> {
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/season/$id_season/credits?api_key=${Config.TMDB_API_KEY}&language=en-US")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = gson.fromJson(json, Credits::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    suspend fun personDetalhes(id: Int): BaseRequest<Person> {
        return suspendCancellableCoroutine { continuation ->
            val request = Request.Builder()
                .url("${baseUrl3}person/$id?api_key=${Config.TMDB_API_KEY}&language=en-US&append_to_response=combined_credits,images,translations")
                .get().build()
            OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
                .newCall(request).enqueue(responseCallback = object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            if (response.isSuccessful) {
                                val json = response.body?.string()
                                val person = Gson().fromJson(json, Person::class.java)
                                continuation.resume(Success(person))
                            } else {
                                continuation.resumeWithException(Exception("Failure"))
                            }
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }
                })
        }
    }

    fun reviewsFilme(idImdb: String?): Observable<ReviewsUflixit> {
        // TODO usar para buscar reviews no the movie
        return Observable.create { subscriber ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("https://uflixit.p.mashape.com/movie/reviews/$idImdb")
                .header("Accept", "application/json")
                .header("X-Mashape-Key", Config.UFLIXI)
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val reviews = gson.fromJson(json, ReviewsUflixit::class.java)
                subscriber.onNext(reviews)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun procuraMulti(query: String?): Observable<MultiSearch> {
        return Observable
            .create { subscriber ->
                if (query.equals("search_suggest_query")) Throwable("")
                val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
                val request = Request.Builder()
                    .url("${baseUrl3}search/multi?api_key=${Config.TMDB_API_KEY}&language=$timeZone&query=$query&page=1&include_adult=false")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val multi = Gson().fromJson(json, MultiSearch::class.java)
                    subscriber.onNext(multi)
                    subscriber.onCompleted()
                } else {
                    subscriber.onError(Throwable(response.message))
                }
            }
    }

    suspend fun getNowPlayingMovies(): BaseRequest<*> {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val idioma = getIdiomaEscolhido(context)
            val request = Request.Builder()
                .url("${baseUrl3}movie/now_playing?api_key=${Config.TMDB_API_KEY}&language=$idioma&page=1")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val listMovie = Gson().fromJson(response.body?.string(),
                                ListaFilmes::class.java)
                            continuation.resume(Success(listMovie))
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }

    suspend fun getMoviePopular(): BaseRequest<*> {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}movie/popular?api_key=${Config.TMDB_API_KEY}&language=${getIdiomaEscolhido(context)}&page=1")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val listaTv = Gson().fromJson(json, ListaFilmes::class.java)
                            continuation.resume(Success(listaTv))
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getUpcoming(): BaseRequest<*> {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}movie/upcoming?api_key=${Config.TMDB_API_KEY}&language=${getIdiomaEscolhido(context)}&page=1")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val listaTv = Gson().fromJson(json, ListaFilmes::class.java)
                            continuation.resume(Success(listaTv))
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }

    suspend fun getAiringToday(): BaseRequest<*> {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/airing_today?api_key=${Config.TMDB_API_KEY}&language=${getIdiomaEscolhido(context)}&page=1")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val list = Gson().fromJson(response.body?.string(),
                                ListaSeries::class.java)
                            continuation.resume(Success(list))
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(Exception("Failure"))
                    }
                }
            })
        }
    }

    suspend fun getPopularTv(): BaseRequest<*> {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("${baseUrl3}tv/popular?api_key=${Config.TMDB_API_KEY}&language=${getIdiomaEscolhido(context)}&page=1")
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val listaTv = Gson().fromJson(json, ListaSeries::class.java)
                            continuation.resume(Success(listaTv))
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getAvaliableMovie(id: String): ReelGood {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("https://api.reelgood.com/v1/movie/$id?sources=amazon_prime%2Chbo%2Chulu_plus%2Cnetflix%2Cstarz%2Cgoogle_plus&free=false")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val lista = Gson().fromJson(json, ReelGood::class.java)
                            continuation.resume(lista)
                        } else {
                            continuation.resumeWithException(Exception("Failure"))
                        }
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getAvaliableShow(id: String): ReelGoodTv {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val request = Request.Builder()
                .url("https://api.reelgood.com/v1/show/$id?sources=amazon_prime%2Chbo%2Chulu_plus%2Cnetflix%2Cstarz%2Cgoogle_plus&free=false")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val lista = Gson().fromJson(json, ReelGoodTv::class.java)
                            continuation.resume(lista)
                        } else {
                            continuation.resumeWithException(Exception(response.message))
                        }
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getTmdbSearch(query: String, page: Int = 1): SearchMulti {
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}search/multi?api_key=${Config.TMDB_API_KEY}&language=${getIdiomaEscolhido(context)}&query=$query&page=$page&include_adult=false")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val lista = gson.fromJson(json, SearchMulti::class.java)
                            cont.resume(lista)
                        } else {
                            cont.resumeWithException(Exception("Failure"))
                        }
                    } catch (e: JsonSyntaxException) {
                        cont.resumeWithException(e)
                    } catch (ex: SocketTimeoutException) {
                        cont.resumeWithException(ex)
                    } catch (ex: Exception) {
                        cont.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getTvSeasonsCd(id: Int, id_season: Int): TvSeasons {
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
            val gson = Gson()
            val request = Request.Builder()
                .url("${baseUrl3}tv/$id/season/$id_season?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = response.body?.string()
                        val lista = gson.fromJson(json, TvSeasons::class.java)
                        cont.resume(lista)
                    } catch (e: JsonSyntaxException) {
                        cont.resumeWithException(e)
                    } catch (ex: SocketTimeoutException) {
                        cont.resumeWithException(ex)
                    } catch (ex: Exception) {
                        cont.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun userGuest(): BaseRequest<GuestSession> {
        return suspendCancellableCoroutine { cont ->
            executeCall("https://api.themoviedb.org/3/authentication/guest_session/new?api_key=${Config.TMDB_API_KEY}", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resume(Failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val json = response.body?.string()
                            val guestSession = gson.fromJson(json, GuestSession::class.java)
                            cont.resume(Success(guestSession))
                        } catch (ex: Exception) {
                            cont.resume(Failure(ex))
                        }
                    } else {
                        cont.resume(Failure(Exception(response.message)))
                    }
                }
            })
        }
    }

    suspend fun ratedMovieGuest(movieDb: MovieDb, guestSession: GuestSession): Any { // Todo Validar erros
        return suspendCancellableCoroutine { cont ->
            executeCall(url = "https://api.themoviedb.org/3/movie/${movieDb.id}/rating?api_key=${Config.TMDB_API_KEY}&guest_session_id=${guestSession.guestSessionId}",
                postRequest = RequestBody.create("application/json".toMediaTypeOrNull(), "{\"value\":${movieDb.nota}}"),
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

    suspend fun getImdb(id: String): BaseRequest<Imdb> {
        return suspendCancellableCoroutine { cont ->
            executeCall("http://www.omdbapi.com/?i=$id&tomatoes=true&r=json&apikey=${Config.OMDBAPI_API_KEY}",
                object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            try {
                                val json = response.body?.string()
                                val imdb = gson.fromJson(json, Imdb::class.java)
                                cont.resume(Success(imdb))
                            } catch (ex: Exception) {
                                cont.resume(Failure(Exception(ex.message)))
                            }
                        } else {
                            cont.resume(Failure(Exception(response.message)))
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        cont.resume(Failure(e))
                    }
                })
        }
    }
}
