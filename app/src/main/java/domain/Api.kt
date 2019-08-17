package domain

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import domain.busca.MultiSearch
import domain.colecao.Colecao
import domain.movie.ListaFilmes
import domain.person.Person
import domain.tvshow.Tvshow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.internal.http2.Http2Reader.Companion.logger
import rx.Observable
import utils.Config
import utils.getIdiomaEscolhido
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine




class Api(val context: Context) {

    private var timeZone: String = "US"
    val baseUrl3 = "https://api.themoviedb.org/3/"
    val baseUrl4 = "https://api.themoviedb.org/4/"

    internal inner class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val t1 = System.nanoTime()
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url, chain.connection(), request.headers))

            val response = chain.proceed(request)

            val t2 = System.nanoTime()
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request.url, (t2 - t1) / 1e6, response.headers))

            return response
        }
    }


    object TIPOBUSCA {

        object FILME {
            val popular: String = "popular"
            val agora: String = "now_playing"
            val chegando: String = "upcoming"
            val melhores: String = "top_rated"
        }

        object SERIE {
            val hoje: String = "airing_today"
            val semana: String = "on_the_air"
            val popular: String = "popular"
            val melhores: String = "top_rated"

        }
    }

    init {
        timeZone = getIdiomaEscolhido(context)
    }

    private fun getKey(): String {
        return if (Random(2).nextInt() % 2 == 0) Config.TMDB_API_KEY else Config.TMDB_API_KEY2
    }

    fun personPopular(pagina: Int): Observable<PersonPopular> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}person/popular?page=" + pagina + "&language=en-US&api_key=" + Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val person = gson.fromJson(json, PersonPopular::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getLista(id: String, pagina: Int = 1): Observable<ListaFilmes> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
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
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("http://www.omdbapi.com/?i=$id&tomatoes=true&r=json&apikey=${Config.OMDBAPI_API_KEY}") //Api de alguem)
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
            val client = OkHttpClient()
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
            val client = OkHttpClient()
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


    fun buscaDeFilmes(tipoDeBusca: String? = TIPOBUSCA.FILME.agora, pagina: Int = 1, local: String = "US"): Observable<ListaFilmes> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            Log.d("Api", "pagina $pagina")
            val url = when (tipoDeBusca) {

                "upcoming", "now_playing" -> {
                    "${baseUrl3}movie/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=${timeZone.replaceBefore("-", "").removeRange(0, 1)}"
                }
                else -> {
                    "${baseUrl3}movie/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=$timeZone"
                }
            }

            val request = Request.Builder()
                    .url(url)
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

    fun buscaDeSeries(tipoDeBusca: String? = TIPOBUSCA.SERIE.popular, pagina: Int = 1, local: String = "US"): Observable<ListaSeries> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=$timeZone")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = gson.fromJson(json, ListaSeries::class.java)
                lista.results
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    private fun getMovie(id: Int): Observable<Movie> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                            "&append_to_response=credits,videos,images,release_dates,similar&include_image_language=en,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val lista = gson.fromJson(json, Movie::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun getMovieVideos(id: Int): Observable<Videos> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/$id/videos?api_key=${Config.TMDB_API_KEY}&language=en-US,null")
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

    fun getTvshowVideos(id: Int): Observable<Videos> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
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
            val client = OkHttpClient()
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
            val client = OkHttpClient()
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
        return suspendCoroutine { cont ->
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                            "&append_to_response=release_dates,external_ids&include_image_language=en,null")
                    .get()
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = response.body?.string()
                        val tvshow = Gson().fromJson(json, Tvshow::class.java)
                        cont.resume(tvshow)
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }

            })
        }
    }

    suspend fun getTvShowEpC(id: Int, idTemp: Int, idEp: Int): EpisodesItem { // Usado em "Seguindo"
        return suspendCoroutine { cont ->
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id/season/$idTemp/episode/$idEp?api_key=${getKey()}" + "&language=$timeZone")
                    .get()
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = response.body?.string()
                        val ep = Gson().fromJson(json, EpisodesItem::class.java)
                        cont.resume(ep)
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }

            })
        }
    }


    fun getColecao(id: Int): Observable<Colecao> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
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

    fun loadMovieComVideo(id: Int): Observable<Movie> {
        return getMovie(id)
                .flatMap { it ->
                    Observable.just(it)
                            .flatMap { video -> Observable.just(video.videos) }
                            .flatMap { videos ->
                                if (videos?.results?.isEmpty()!!) {
                                    Observable.zip(Observable.just(it), getMovieVideos(id)
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
            val client = OkHttpClient()
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
        return suspendCoroutine { cont ->
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id/season/$id_season?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                    .get()
                    .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = response.body?.string()
                        val tvshow = Gson().fromJson(json, TvSeasons::class.java)
                        cont.resume(tvshow)
                    } catch (ex: Exception) {
                        cont.resumeWithException(Throwable(ex.message))
                    }
                }

            })
        }
    }

    fun getTvCreditosTemporada(id: Int, id_season: Int): Observable<Credits> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
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

    fun personDetalhes(id: Int): Observable<Person> {

        return Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}person/$id?api_key=${Config.TMDB_API_KEY}&language=en-US+" + "&append_to_response=combined_credits,images")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val person = gson.fromJson(json, Person::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message))
            }
        }
    }

    fun reviewsFilme(idImdb: String?): Observable<ReviewsUflixit> {
        return Observable.create { subscriber ->
            val client = OkHttpClient()
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
                    val client = OkHttpClient()
                    val gson = Gson()
                    val request = Request.Builder()
                            .url("${baseUrl3}search/multi?api_key=${Config.TMDB_API_KEY}&language=$timeZone&query=$query&page=1&include_adult=false")
                            .get()
                            .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        val multi = gson.fromJson(json, MultiSearch::class.java)
                        subscriber.onNext(multi)
                        subscriber.onCompleted()
                    } else {
                        subscriber.onError(Throwable(response.message))
                    }

                }
    }

    suspend fun getNowPlayingMovies(): ListaFilmes {
        return suspendCoroutine { continuation ->
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
                        val json = response.body?.string()
                        val gson = Gson()
                        val listaFilmes = gson.fromJson(json, ListaFilmes::class.java)
                        continuation.resume(listaFilmes)
                    } catch (Ex: Exception) {
                        continuation.resumeWithException(Ex)
                    } catch (ex: SocketTimeoutException) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getMoviePopular(): ListaFilmes {
        return suspendCoroutine { continuation ->
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
                        val json = response.body?.string()
                        val listaTv = Gson().fromJson(json, ListaFilmes::class.java)
                        continuation.resume(listaTv)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    } catch (ex: SocketTimeoutException) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getUpcoming(): ListaFilmes {
        return suspendCoroutine { continuation ->
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
                        val json = response.body?.string()
                        val listaTv = Gson().fromJson(json, ListaFilmes::class.java)
                        continuation.resume(listaTv)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    } catch (ex: SocketTimeoutException) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getAiringToday(): ListaSeries {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient()
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
                        val json = response.body?.string()
                        val listaTv = Gson().fromJson(json, ListaSeries::class.java)
                        continuation.resume(listaTv)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    } catch (ex: SocketTimeoutException) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }


    suspend fun getPopularTv(): ListaSeries {
        return suspendCancellableCoroutine { continuation ->
            val client = OkHttpClient()
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
                        val json = response.body?.string()
                        val listaTv = Gson().fromJson(json, ListaSeries::class.java)
                        continuation.resume(listaTv)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    } catch (ex: SocketTimeoutException) {
                        continuation.resumeWithException(ex)
                    }
                }
            })
        }
    }

    suspend fun getTvSeasonsC(id: Int, id_season: Int): TvSeasons {
        return suspendCancellableCoroutine { cont ->
            val client = OkHttpClient()
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
                    } catch (ex: Exception) {
                        cont.resumeWithException(ex)
                    }
                }
            })
        }
    }
}
