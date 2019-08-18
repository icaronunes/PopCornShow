package main

import BaseViewModel
import androidx.lifecycle.MutableLiveData
import applicaton.FilmeApplication
import domain.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException

class MainViewModel : BaseViewModel() {

    val data = MutableLiveData<MainModel>()

    fun getTopoLista(application: FilmeApplication) {
        GlobalScope.launch(coroutineContext) {
            try {
                val movies = async(coroutineContext) { Api(application).getNowPlayingMovies() }
                val tvshow = async(coroutineContext) { Api(application).getAiringToday() }
                if (movies.isActive && tvshow.isActive)
                    data.value = MainModel.Data(Pair(movies.await(), tvshow.await()))
            } catch (ex: Exception) {
                ops(application)
            } catch (ex: ConnectException) {
                ops(application)
            }
        }
    }

    sealed class MainModel {
        class Data(val data: Pair<ListaFilmes, ListaSeries>) : MainModel()
    }


}