package main

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import applicaton.BaseViewModel
import br.com.icaro.filme.BuildConfig
import domain.ListaSeries
import domain.movie.ListaFilmes

class MainViewModel(override val app: Application) : BaseViewModel(app), MainBusinessListener {

    private val business: MainBusiness = MainBusiness(app, this, this)

    private val _data = MutableLiveData<MainModel>()
    val data: LiveData<MainModel>
        get() = _data

    fun getTopoLista() {
        business.setTopLista()
    }

    fun novidade() {
        business.setNovidade()
    }

    override fun animation(visible: Boolean) {
        _data.value = MainModel.VisibleAnimed(visible)
    }

    override fun setNovidade(isNews: MainModel) {
        _data.value = isNews
    }

    override fun setTopLista(movies: ListaFilmes, tvShows: ListaSeries) {
        _data.value = MainModel.Data(Pair(movies, tvShows))
    }

    override fun getOps(){
        ops()
    }

    sealed class MainModel {
        class Data(val data: Pair<ListaFilmes, ListaSeries>) : MainModel()
        class VisibleAnimed(val visible: Boolean) : MainModel()
        object News : MainModel()
    }
}