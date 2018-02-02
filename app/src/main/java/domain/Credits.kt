package domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Credits(

	@field:SerializedName("cast")
	val cast: List<CastItem?>? = null,

	@field:SerializedName("crew")
	val crew: List<CrewItem?>? = null
): Serializable