package domain

import com.google.gson.annotations.SerializedName
import domain.movie.ListaItemFilme
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class CompanyFilmes(

		@field:SerializedName("id")
	val id: Int? = null,

		@field:SerializedName("page")
	val page: Int? = null,

		@field:SerializedName("total_pages")
	val totalPages: Int? = null,

		@field:SerializedName("results")
	val results: List<ListaItemFilme?>? = null,

		@field:SerializedName("total_results")
	val totalResults: Int? = null
)