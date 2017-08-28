package domain

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import utils.Config

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

}