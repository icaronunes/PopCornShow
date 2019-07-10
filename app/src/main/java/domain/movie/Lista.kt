package domain.movie

import com.google.gson.annotations.SerializedName
import domain.ViewType
import utils.Constantes
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Lista(

        //@field:SerializedName("object_ids")
        //val objectIds: ObjectIds? = null,

       // 	@field:SerializedName("comments")
       // 	val comments: Comments? = null,

        @field:SerializedName("iso_3166_1")
        val iso31661: String = "",

        @field:SerializedName("description")
        val description: String = "",

        @field:SerializedName("runtime")
        val runtime: Int = 0,

        @field:SerializedName("average_rating")
        val averageRating: Double = 0.0,

        @field:SerializedName("total_pages")
        val totalPages: Int = 0,

        @field:SerializedName("sort_by")
        val sortBy: String = "null",

        @field:SerializedName("iso_639_1")
        val iso6391: String = "null",

        @field:SerializedName("created_by")
        val createdBy: CreatedBy? = null,

        @field:SerializedName("poster_path")
        val posterPath: String = "",

        @field:SerializedName("total_results")
        val totalResults: Int = 0,

        @field:SerializedName("backdrop_path")
        val backdropPath: String = "",

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
