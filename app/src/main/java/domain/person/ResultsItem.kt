package domain.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ResultsItem(

	@field:SerializedName("file_path")
	val filePath: String? = null,

	@field:SerializedName("aspect_ratio")
	val aspectRatio: Double? = null,

	@field:SerializedName("media_type")
	val mediaType: String? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("media")
	val media: Media? = null,

	@field:SerializedName("iso_639_1")
	val iso6391: String? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("height")
	val height: Int? = null
): Serializable