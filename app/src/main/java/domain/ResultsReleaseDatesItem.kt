package domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ResultsReleaseDatesItem(

	@field:SerializedName("release_dates")
	val releaseDates: List<ReleaseDatesItem?>? = null,

	@field:SerializedName("iso_3166_1")
	val iso31661: String? = null
): Serializable