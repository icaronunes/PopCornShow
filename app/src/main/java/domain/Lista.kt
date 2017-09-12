package domain

import com.google.gson.annotations.SerializedName
import utils.Constantes
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Lista(

        //@field:SerializedName("object_ids")
        //val objectIds: ObjectIds? = null,

        //	@field:SerializedName("comments")
        //	val comments: Comments? = null,

        @field:SerializedName("iso_3166_1")
        val iso31661: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("runtime")
        val runtime: Int? = null,

        @field:SerializedName("average_rating")
        val averageRating: Double? = null,

        @field:SerializedName("total_pages")
        val totalPages: Int? = null,

        @field:SerializedName("sort_by")
        val sortBy: String? = null,

        @field:SerializedName("iso_639_1")
        val iso6391: String? = null,

        @field:SerializedName("created_by")
        val createdBy: CreatedBy? = null,

        @field:SerializedName("poster_path")
        val posterPath: String? = null,

        @field:SerializedName("total_results")
        val totalResults: Int? = null,

        @field:SerializedName("backdrop_path")
        val backdropPath: String? = null,

        @field:SerializedName("revenue")
        val revenue: String? = null,

        @field:SerializedName("public")
        val jsonMemberPublic: Boolean? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("page")
        val page: Int? = null,

        @field:SerializedName("results")
        val results: List<ListaItemFilme?>? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

@Generated("com.robohorse.robopojogenerator")
data class ListaItemFilme(

        @field:SerializedName("overview")
        val overview: String? = null,

        @field:SerializedName("original_language")
        val originalLanguage: String? = null,

        @field:SerializedName("original_title")
        val originalTitle: String? = null,

        @field:SerializedName("original_name")
        val original_name: String? = null,

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

        @field:SerializedName("media_type")
        val mediaType: String? = null,

        @field:SerializedName("release_date")
        val releaseDate: String? = null,

        @field:SerializedName("vote_average")
        val voteAverage: Double? = null,

        @field:SerializedName("popularity")
        val popularity: Double? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("adult")
        val adult: Boolean? = null,

        @field:SerializedName("vote_count")
        val voteCount: Int? = null,

        @field:SerializedName("first_air_date")
        val first_air_date: String? = null,

        @field:SerializedName("name")
        val name: String? = null


): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}

@Generated("com.robohorse.robopojogenerator")
data class CreatedBy(

        @field:SerializedName("gravatar_hash")
        val gravatarHash: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("username")
        val username: String? = null
): ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}
