package domain

import com.google.gson.annotations.SerializedName
import utils.Constantes
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ListaSeries(

        @field:SerializedName("page")
        val page: Int? = null,

        @field:SerializedName("total_pages")
        val totalPages: Int? = null,

        @field:SerializedName("results")
        val results: List<ListaItemSerie> = mutableListOf<ListaItemSerie>(),

        @field:SerializedName("total_results")
        val totalResults: Int? = null
) : ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

data class ListaItemSerie(

        @field:SerializedName("first_air_date")
        val firstAirDate: String? = null,

        @field:SerializedName("overview")
        val overview: String? = null,

        @field:SerializedName("original_language")
        val originalLanguage: String? = null,

        @field:SerializedName("genre_ids")
        val genreIds: List<Int?>? = null,

        @field:SerializedName("poster_path")
        val posterPath: String? = null,

        @field:SerializedName("origin_country")
        val originCountry: List<String?>? = null,

        @field:SerializedName("backdrop_path")
        val backdropPath: String? = null,

        @field:SerializedName("original_name")
        val originalName: String? = null,

        @field:SerializedName("popularity")
        val popularity: Double? = null,

        @field:SerializedName("vote_average")
        val voteAverage: Float? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("vote_count")
        val voteCount: Int? = null
) : ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}