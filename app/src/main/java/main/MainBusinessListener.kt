package main

import domain.ListaSeries
import domain.movie.ListaFilmes

interface MainBusinessListener {
    fun setTopList(movies: ListaFilmes, tvShows: ListaSeries)
    fun getOps()
    fun setNovidade(isNews: MainViewModel.MainModel)
    fun animation(visible: Boolean = true)
}
