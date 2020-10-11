package domain

import com.google.gson.annotations.SerializedName
import domain.movie.ListaItemFilme
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Company(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("page")
    val page: Int,
    @field:SerializedName("total_pages")
    val totalPages: Int,
    @field:SerializedName("results")
    val results: List<ListaItemFilme?>? = null,
    @field:SerializedName("total_results")
    val totalResults: Int
)
