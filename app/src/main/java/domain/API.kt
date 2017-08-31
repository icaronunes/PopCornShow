package domain

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import utils.Config
import java.util.*

class API {
    fun PersonPopular(pagina: Int): Observable<PersonPopular> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/person/popular?page="+pagina+"&language=en-US&api_key="+Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val json = response.body()?.string()
                val person = gson.fromJson(json, PersonPopular::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun OscarLista(id: Int, pagina : Int = 1): Observable<Lista> {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/4/list/"+id+"?page="+pagina+"&api_key="+Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val json = response.body()?.string()
                val lista = gson.fromJson(json, Lista::class.java)
                subscriber.onNext(lista)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun BuscaNetFlix(query: String, days : Int = 14, andor: String = "and", audio: String = "Any",
              countrylist: String = "all", endImdbRate: Int = 10, EndNetfRate: Int = 10, genreid:Int = 0,
              imdbvotes: Int = 0, page: Int = 1, startImdbRate: Int = 0, startNetRate: Int = 0,
              sortby: String = "Relevance", subtitle: String = "Any", startYear: Int = 1900, typeVideo: String = "Any" ): Observable<PersonPopular> {
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
            if (response.isSuccessful){
                val json = response.body()?.string()
                val person = gson.fromJson(json, PersonPopular::class.java)
                subscriber.onNext(person)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    fun getCompany(id_produtora: Int): Observable<Company>  {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/company/$id_produtora?api_key="+Config.TMDB_API_KEY)
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val json = response.body()?.string()
                val company = gson.fromJson(json, Company::class.java)
                subscriber.onNext(company)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }


    }

    fun getCompanyFilmes(company_id: Int, pagina: Int = 1): Observable<CompanyFilmes>  {
        return rx.Observable.create { subscriber ->
            val client = OkHttpClient()
            val gson = Gson()
            val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/company/$company_id/movies?page=$pagina&api_key=${Config.TMDB_API_KEY}&language=en-US")
                    .get()
                    .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                val json = response.body()?.string()
                val companyFilmes = gson.fromJson(json, CompanyFilmes::class.java)
                subscriber.onNext(companyFilmes)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }


    }

}