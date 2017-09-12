package domain

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Similar(

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("results")
	val resultsSimilar: List<ResultsSimilarItem?>? = null,

	@field:SerializedName("total_results")
	val totalResults: Int? = null
)