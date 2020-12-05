package domain.busca

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")//TOdo Apagar
data class MultiSearch(

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("total_pages")
    val totalPages: Int? = null,

    @field:SerializedName("results")
    val results: List<ResultsItem?>? = null,

    @field:SerializedName("total_results")
    val totalResults: Int? = null
) : Serializable
