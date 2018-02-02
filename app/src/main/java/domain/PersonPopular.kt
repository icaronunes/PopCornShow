package domain

import com.google.gson.annotations.SerializedName
import utils.Constantes
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class PersonPopular(

        @field:SerializedName("page")
        val page: Int? = null,

        @field:SerializedName("total_pages")
        val totalPages: Int? = null,

        @field:SerializedName("results")
        val results: List<PersonItem?>? = null,

        @field:SerializedName("total_results")
        val totalResults: Int? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

@Generated("com.robohorse.robopojogenerator")
data class PersonItem(

        @field:SerializedName("known_for")
        val knownFor: List<KnownForItem?>? = null,

        @field:SerializedName("popularity")
        val popularity: Double? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("profile_path")
        val profilePath: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("adult")
        val adult: Boolean? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

@Generated("com.robohorse.robopojogenerator")
data class KnownForItem(

        @field:SerializedName("overview")
        val overview: String? = null,

        @field:SerializedName("original_language")
        val originalLanguage: String? = null,

        @field:SerializedName("original_title")
        val originalTitle: String? = null,

        @field:SerializedName("video")
        val video: Boolean? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("genre_ids")
        val genreIds: List<Int?>? = null,

        @field:SerializedName("poster_path")
        val posterPath: String? = null,

        @field:SerializedName("backdrop_path")
        val backdropPath: String? = null,

        @field:SerializedName("release_date")
        val releaseDate: String? = null,

        @field:SerializedName("media_type")
        val mediaType: String? = null,

        @field:SerializedName("popularity")
        val popularity: Double? = null,

        @field:SerializedName("vote_average")
        val voteAverage: Double? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("adult")
        val adult: Boolean? = null,

        @field:SerializedName("vote_count")
        val voteCount: Int? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}
