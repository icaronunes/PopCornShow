package domain.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class CombinedCredits(

	@field:SerializedName("cast")
	val cast: List<CastItem?>? = null,

	@field:SerializedName("crew")
	val crew: List<CrewItem?>? = null
): Serializable