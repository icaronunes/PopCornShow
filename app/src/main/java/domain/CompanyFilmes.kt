package domain

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class CompanyFilmes(

        @field:SerializedName("id")
	val id: Int? = null,

        @field:SerializedName("page")
	val page: Int? = null,

        @field:SerializedName("total_pages")
	val totalPages: Int? = null,

        @field:SerializedName("results")
	val results: List<ListaItemSerie?>? = null,

        @field:SerializedName("total_results")
	val totalResults: Int? = null
)