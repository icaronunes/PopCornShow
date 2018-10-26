package domain

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import domain.busca.MultiSearch
import domain.colecao.Colecao
import domain.movie.Lista
import domain.movie.ListaFilmes
import domain.person.Person
import domain.tvshow.Tvshow
import okhttp3.*
import rx.Observable
import utils.Config
import utils.getIdiomaEscolhido
import java.io.IOException
import java.util.*
import kotlin.coroutines.experimental.suspendCoroutine


class API(context: Context) {

    private var timeZone: String = "US"
    val baseUrl3 = "https://api.themoviedb.org/3/"
    val baseUrl4 = "https://api.themoviedb.org/4/"


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


    fun PersonPopular(pagina: Int): Observable<PersonPopular> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}person/popular?page=" + pagina + "&language=en-US&api_key=" + Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val person = gson.fromJson(json, PersonPopular::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getLista(id: String, pagina: Int = 1): Observable<Lista> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl4}list/" + id + "?page=" + pagina + "&api_key=" + Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val lista = gson.fromJson(json, Lista::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getOmdbpi(id: String?): Observable<Imdb> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("http://www.omdbapi.com/?i=$id&tomatoes=true&r=json&apikey=${Config.OMDBAPI_API_KEY}") //API de alguem)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val imdb = gson.fromJson(json, Imdb::class.java)
                subscriber.onNext(imdb)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun buscaNetFlix(query: String = "", days: Int = 14, andor: String = "and", audio: String = "Any",
                     countrylist: String = "all", endImdbRate: Int = 10, EndNetfRate: Int = 10, genreid: Int = 0,
                     imdbvotes: Int = 0, page: Int = 1, startImdbRate: Int = 0, startNetRate: Int = 0,
                     sortby: String = "Relevance", subtitle: String = "Any", startYear: Int = 1900, typeVideo: String = "Any"): Observable<PersonPopular> {
        return rx.Observable.create { subscriber ->
            val year = Calendar.getInstance()[Calendar.YEAR]
            val client = OkHttpClient()
            val gson = Gson()

            val request = Request.Builder()
                    .url("https://unogs-unogs-v1.p.mashape.com/aaapi.cgi?" +
                            "q=$query-!$startYear,$year-!$startNetRate,$EndNetfRate-!$startImdbRate," +
                            "$endImdbRate-!$genreid-!$typeVideo-!$audio-!$subtitle-!" +
                            "$imdbvotes-!{downloadable}&t=ns&cl=client&st=adv&ob=$sortby&p=$page&sa=$andor")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val person = gson.fromJson(json, PersonPopular::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getCompany(id_produtora: Int): Observable<Company> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}company/$id_produtora?api_key=" + Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val company = gson.fromJson(json, Company::class.java)
                subscriber.onNext(company)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getCompanyFilmes(company_id: Int, pagina: Int = 1): Observable<CompanyFilmes> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}company/$company_id/movies?page=$pagina&api_key=${Config.TMDB_API_KEY}&language=$timeZone")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val companyFilmes = gson.fromJson(json, CompanyFilmes::class.java)
                subscriber.onNext(companyFilmes)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }


    }


    fun BuscaDeFilmes(tipoDeBusca: String? = TIPOBUSCA.FILME.agora, pagina: Int = 1, local: String = "US"): Observable<ListaFilmes> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()

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
                val json = response.body()?.string()
                val lista = gson.fromJson(json, ListaFilmes::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun BuscaDeSeries(tipoDeBusca: String? = TIPOBUSCA.SERIE.popular, pagina: Int = 1, local: String = "US"): Observable<ListaSeries> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=$timeZone")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val lista = gson.fromJson(json, ListaSeries::class.java)
                lista.results
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    private fun getMovie(id: Int): Observable<Movie> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                            "&append_to_response=credits,videos,images,release_dates,similar&include_image_language=en,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val lista = gson.fromJson(json, Movie::class.java)
                lista
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getMovieVideos(id: Int): Observable<Videos> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/$id/videos?api_key=${Config.TMDB_API_KEY}&language=en-US,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val videos = gson.fromJson(json, Videos::class.java)

                subscriber.onNext(videos)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTvshowVideos(id: Int): Observable<Videos> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id/videos?api_key=${Config.TMDB_API_KEY}&language=en-US,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val videos = gson.fromJson(json, Videos::class.java)

                subscriber.onNext(videos)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTvShow(id: Int): Observable<Tvshow> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                            "&append_to_response=credits,videos,images,release_dates,similar,external_ids&include_image_language=en,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val tvshow = gson.fromJson(json, Tvshow::class.java)
                subscriber.onNext(tvshow)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTvShowLite(id: Int): Observable<Tvshow> { // Usado em "Seguindo"
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id?api_key=${Config.TMDB_API_KEY}" + "&language=$timeZone" +
                            "&append_to_response=release_dates,external_ids&include_image_language=en,null")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val tvshow = gson.fromJson(json, Tvshow::class.java)
                subscriber.onNext(tvshow)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }


    fun getColecao(id: Int): Observable<Colecao> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}collection/$id?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val colecao = gson.fromJson(json, Colecao::class.java)

                subscriber.onNext(colecao)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
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
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id/season/$id_season?api_key=${Config.TMDB_API_KEY}&language=$timeZone,en")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val lista = gson.fromJson(json, TvSeasons::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getTvCreditosTemporada(id: Int, id_season: Int): Observable<Credits> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$id/season/$id_season/credits?api_key=${Config.TMDB_API_KEY}&language=en-US")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val lista = gson.fromJson(json, Credits::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun personDetalhes(id: Int): Observable<Person> {

        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}person/$id?api_key=${Config.TMDB_API_KEY}&language=en-US+" + "&append_to_response=combined_credits,images")
                    //,tagged_images,external_ids")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val person = gson.fromJson(json, Person::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun reviewsFilme(idImdb: String?): Observable<ReviewsUflixit> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://uflixit.p.mashape.com/movie/reviews/$idImdb")
                    .header("Accept", "application/json")
                    .header("X-Mashape-Key", Config.UFLIXI)
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()?.string()
                val reviews = gson.fromJson(json, ReviewsUflixit::class.java)
                subscriber.onNext(reviews)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun procuraMulti(query: String?): Observable<MultiSearch> {
        return rx.Observable
                .create(Observable.OnSubscribe { subscriber ->
                    if (query.equals("search_suggest_query")) Throwable("")
                    val client = OkHttpClient()
                    val gson = Gson()
                    val request = Request.Builder()
                            .url("${baseUrl3}search/multi?api_key=${Config.TMDB_API_KEY}&language=${timeZone}&query=${query}&page=1&include_adult=false")
                            .get()
                            .build()
                    val response = client.newCall(request).execute()
                    Log.d(this.toString(), "entrou... - $query")
                    if (response.isSuccessful) {
                        val json = response.body()?.string()
                        val multi = gson.fromJson(json, MultiSearch::class.java)
                        subscriber.onNext(multi)
                        subscriber.onCompleted()
                    } else {
                        subscriber.onError(Throwable(response.message()))
                    }

                })
    }

    suspend fun getNowPlayingMovies(): ListaFilmes {
        return suspendCoroutine { continuation ->
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/now_playing?api_key=${Config.TMDB_API_KEY}&language=$timeZone&page=1")
                    .get()
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body()?.string()
                    try {
                        val gson = Gson()
                        val listaFilmes = gson.fromJson(json, ListaFilmes::class.java)
                        continuation.resume(listaFilmes)
                    } catch (Ex: Exception) {
                        continuation.resumeWithException(Ex)
                    }
                }
            })
        }
    }

    suspend fun getAiringToday(): ListaSeries {
        return suspendCoroutine { continuation ->
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/airing_today?api_key=${Config.TMDB_API_KEY}&language=$timeZone&page=1")
                    .get()
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(Throwable(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body()?.string()
                    try {
                        val listaTv = Gson().fromJson(json, ListaSeries::class.java)
                        continuation.resume(listaTv)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(Throwable(ex.message))
                    }
                }
            })
        }
    }

}
