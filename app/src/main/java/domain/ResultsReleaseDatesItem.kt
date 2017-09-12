package domain

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class ResultsReleaseDatesItem(

	@field:SerializedName("release_dates")
	val releaseDates: List<ReleaseDatesItem?>? = null,

	@field:SerializedName("iso_3166_1")
	val iso31661: String? = null
)