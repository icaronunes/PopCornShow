package main

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import applicaton.BaseViewModel
import domain.Api
import domain.ListaSeries
import domain.movie.ListaFilmes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import utils.UtilsApp
import java.net.ConnectException

class MainFragViewModel(application: Application) : BaseViewModel(application) {

    private val _data = MutableLiveData<MainFragModel>()
    val data: LiveData<MainFragModel>
        get() = _data

    fun setMoviesUpComing() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) {
            getUpComing()
        } else {
            noInternet()
        }
    }

    private fun getUpComing() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                val upComing = async(Dispatchers.IO) {
                    Api(app).getUpcoming()
                }
                _data.value = MainFragModel.ModelUpComing(upComing.await())
            } catch (ex: ConnectException) {
                ops()
                job.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                ops()
                job.cancelAndJoin()
            }
        }
    }

    fun setMoviesPopular() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) {
            getMoviePopular()
        } else {
            noInternet()
        }
    }

    private fun getMoviePopular() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                val popular = async(Dispatchers.IO) {
                    Api(app).getMoviePopular()
                }
                _data.value = MainFragModel.ModelPopularMovie(popular.await())
            } catch (ex: ConnectException) {
                ops()
                job.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                ops()
                job.cancelAndJoin()
            }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun setAiringToday() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) {
            getAiringToday()
        } else {
            noInternet()
        }
    }

    private fun getAiringToday() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                val airTv = async(Dispatchers.IO) {
                    Api(app).getAiringToday()
                }
                _data.value = MainFragModel.ModelAiringToday(airTv.await())
            } catch (ex: ConnectException) {
                ops()
                job.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                ops()
                job.cancelAndJoin()
            }
        }
    }

    fun getPopularTv() {
        if (UtilsApp.isNetWorkAvailable(getApplication())) {
            setPopularTv()
        } else {
            noInternet()
        }
    }

    private fun setPopularTv() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                val popular = async(Dispatchers.IO) {
                    Api(app).getPopularTv()
                }
                _data.value = MainFragModel.ModelPopularTvshow(popular.await())
            } catch (ex: ConnectException) {
                ops()
                job.cancelAndJoin()
            } catch (ex: java.lang.Exception) {
                ops()
                job.cancelAndJoin()
            }
        }
    }

    sealed class MainFragModel {
        class ModelPopularMovie(val movies: ListaFilmes) : MainFragModel()
        class ModelUpComing(val movies: ListaFilmes) : MainFragModel()
        class ModelAiringToday(val tvshows: ListaSeries) : MainFragModel()
        class ModelPopularTvshow(val tvshows: ListaSeries) : MainFragModel()
    }
}
