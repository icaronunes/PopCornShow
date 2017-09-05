package domain

import com.google.gson.Gson
import info.movito.themoviedbapi.model.config.Timezone
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import utils.Config
import utils.UtilsApp
import java.util.*

class API {

    private var timeZone: Timezone? = null

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

    val baseUrl3 = "https://api.themoviedb.org/3/"
    val baseUrl4 = "https://api.themoviedb.org/4/"

    fun PersonPopular(pagina: Int): Observable<PersonPopular> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/person/popular?page=" + pagina + "&language=en-US&api_key=" + Config.TMDB_API_KEY)
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

    fun OscarLista(id: Int, pagina: Int = 1): Observable<Lista> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/4/list/" + id + "?page=" + pagina + "&api_key=" + Config.TMDB_API_KEY)
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

    fun BuscaNetFlix(query: String, days: Int = 14, andor: String = "and", audio: String = "Any",
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
                    .url("https://api.themoviedb.org/3/company/$id_produtora?api_key=" + Config.TMDB_API_KEY)
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
                    .url("https://api.themoviedb.org/3/company/$company_id/movies?page=$pagina&api_key=${Config.TMDB_API_KEY}&language=en-US")
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


    fun BuscaDeFilmes(tipoDeBusca: String? = TIPOBUSCA.FILME.agora, local: String? = "US", pagina: Int = 1, timeZone: Timezone? = Timezone("US", "US")): Observable<ListaFilmes> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}movie/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=${timeZone?.country}")
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

    fun BuscaDeSeries(tipoDeBusca: String? = TIPOBUSCA.SERIE.popular, local: String = "US", pagina: Int = 1, timeZone: Timezone? = Timezone("US", "US")): Observable<ListaSeries> {
        // tipos de buscas - "now_playing", "upcoming", "top_rated", "popular" - Mude o tipo, para mudar busca
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            if (timeZone != null)
            this.timeZone = UtilsApp.getTimezone()
            val gson = Gson()
            val request = Request.Builder()
                    .url("${baseUrl3}tv/$tipoDeBusca?api_key=${Config.TMDB_API_KEY}&language=$local&page=$pagina&region=${timeZone?.country}")
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

}