package domain.movie

import com.google.gson.annotations.SerializedName
import domain.ViewType
import utils.Constantes

/**
 * Created by icaro on 03/09/17.
 */
data class ListaFilmes(

        @field:SerializedName("page")
        val page: Int = 0,

        @field:SerializedName("total_pages")
        val totalPages: Int = 0,

        @field:SerializedName("dates")
        val dates: Date? = null,

        @field:SerializedName("results")
        val results: List<ListaItemFilme?> = mutableListOf<ListaItemFilme>(),

        @field:SerializedName("total_results")
        val totalResults: Int = 0
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

