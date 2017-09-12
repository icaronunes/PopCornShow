package domain

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class Credits(

	@field:SerializedName("cast")
	val cast: List<CastItem?>? = null,

	@field:SerializedName("crew")
	val crew: List<CrewItem?>? = null
)