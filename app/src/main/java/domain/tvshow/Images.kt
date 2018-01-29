package domain.tvshow

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Images(

	@field:SerializedName("backdrops")
	val backdrops: List<domain.BackdropsItem?>? = null,

	@field:SerializedName("posters")
	val posters: List<domain.PostersItem?>? = null
): Serializable