package domain

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class BelongsToCollection(

	@field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null
)