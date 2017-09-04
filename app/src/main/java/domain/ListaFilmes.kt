package domain

import com.google.gson.annotations.SerializedName
import utils.Constantes

/**
 * Created by icaro on 03/09/17.
 */
data class ListaFilmes(

        @field:SerializedName("page")
        val page: Int? = null,

        @field:SerializedName("total_pages")
        val totalPages: Int? = null,

        @field:SerializedName("dates")
        val dates: Date? = null,

        @field:SerializedName("results")
        val results: List<ListaItemFilme?>? = null,

        @field:SerializedName("total_results")
        val totalResults: Int? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

data class Date(

        @field:SerializedName("maximum")
        val maximum: String? = null,

        @field:SerializedName("minimum")
        val minimum: String? = null
) : ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}