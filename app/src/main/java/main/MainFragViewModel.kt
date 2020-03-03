package main

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.Success
import utils.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import utils.UtilsApp

class MainFragViewModel(application: Application) : BaseViewModel(application) {

    private val _data = MutableLiveData<MainFragModel>()
    val data: LiveData<MainFragModel>
        get() = _data

    fun setMoviesUpComing() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) getUpComing() else noInternet()
    }

    private fun getUpComing() {
        GlobalScope.launch(coroutineContext) {
            val upComing = async(Dispatchers.IO) {
                Api(app).getUpcoming() as Success<ListaFilmes>
            }
            _data.value = MainFragModel.ModelUpComing(upComing.await().result)
        }
    }

    fun setMoviesPopular() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) getMoviePopular() else noInternet()
    }

    private fun getMoviePopular() {
        GlobalScope.launch(coroutineContext) {
            val popular = async(Dispatchers.IO) {
                Api(app).getMoviePopular() as Success<ListaFilmes>
            }
            _data.value = MainFragModel.ModelPopularMovie(popular.await().result)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun setAiringToday() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) getAiringToday() else noInternet()
    }

    private fun getAiringToday() {
        GlobalScope.launch(coroutineContext) {
            val airTv = async(Dispatchers.IO) {
                Api(app).getAiringToday() as Success<ListaSeries>
            }
            _data.value = MainFragModel.ModelAiringToday(airTv.await().result)
        }
    }

    fun getPopularTv() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) setPopularTv() else noInternet()
    }

    private fun setPopularTv() {
        GlobalScope.launch(coroutineContext) {
            val popular = async(Dispatchers.IO) {
                Api(app).getPopularTv() as Success<ListaSeries>
            }
            _data.value = MainFragModel.ModelPopularTvshow(popular.await().result)
        }
    }

    sealed class MainFragModel {
        class ModelPopularMovie(val movies: ListaFilmes) : MainFragModel()
        class ModelUpComing(val movies: ListaFilmes) : MainFragModel()
        class ModelAiringToday(val tvshows: ListaSeries) : MainFragModel()
        class ModelPopularTvshow(val tvshows: ListaSeries) : MainFragModel()
    }
}
