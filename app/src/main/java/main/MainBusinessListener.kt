package main

import domain.ListaSeries
import domain.movie.ListaFilmes

interface MainBusinessListener {
    fun setTopList(movies: ListaFilmes, tvShows: ListaSeries)
    fun getOps()
    fun setNovidade(isNews: Boolean)
    fun animation(visible: Boolean = true)
}
