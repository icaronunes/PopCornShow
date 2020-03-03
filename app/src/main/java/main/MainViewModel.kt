package main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import domain.ListaSeries
import domain.movie.ListaFilmes

class MainViewModel(override val app: Application) : BaseViewModel(app), MainBusinessListener {

    private val business: MainBusiness = MainBusiness(app, this, this)

    private val _data = MutableLiveData<MainModel>()
    val data: LiveData<MainModel>
        get() = _data

    fun getTopList() = business.setTopList()
    fun news() = business.setNews()
    override fun getOps() = ops()

    override fun animation(visible: Boolean) {
        _data.value = MainModel.VisibleAnimed(visible)
    }

    override fun setNovidade(isNews: MainModel) {
        _data.value = isNews
    }

    override fun setTopList(movies: ListaFilmes, tvShows: ListaSeries) {
        _data.value = MainModel.Data(Pair(movies, tvShows))
    }

    sealed class MainModel {
        class Data(val data: Pair<ListaFilmes, ListaSeries>) : MainModel()
        class VisibleAnimed(val visible: Boolean) : MainModel()
        object News : MainModel()
    }
}
