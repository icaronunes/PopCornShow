package domain

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class PostersItem(

	@field:SerializedName("aspect_ratio")
	val aspectRatio: Double? = null,

	@field:SerializedName("file_path")
	val filePath: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("iso_639_1")
	val iso6391: String? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("height")
	val height: Int? = null
)