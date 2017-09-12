package domain

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Videos(

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("results")
        val results: MutableList<ResultsVideosItem?>? = null
)