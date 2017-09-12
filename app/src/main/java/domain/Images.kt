package domain

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Images(

	@field:SerializedName("backdrops")
	val backdrops: List<BackdropsItem?>? = null,

	@field:SerializedName("posters")
	val posters: List<PostersItem?>? = null
)