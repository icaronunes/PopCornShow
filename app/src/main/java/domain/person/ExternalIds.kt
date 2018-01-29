package domain.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ExternalIds(

	@field:SerializedName("imdb_id")
	val imdbId: String? = null,

	@field:SerializedName("freebase_mid")
	val freebaseMid: String? = null,

	@field:SerializedName("freebase_id")
	val freebaseId: String? = null,

	@field:SerializedName("twitter_id")
	val twitterId: String? = null,

	@field:SerializedName("instagram_id")
	val instagramId: String? = null,

	@field:SerializedName("tvrage_id")
	val tvrageId: Int? = null,

	@field:SerializedName("facebook_id")
	val facebookId: String? = null
): Serializable