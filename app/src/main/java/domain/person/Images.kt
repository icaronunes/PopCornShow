package domain.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Images(

	@field:SerializedName("profiles")
	val profiles: List<ProfilesItem?>? = null
): Serializable