package domain.tvshow

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LastEpisodeAir(
		@field:SerializedName("air_date")
		val airDate: String?,
		@field:SerializedName("episode_number")
		val episodeNumber: Int?,
		@field:SerializedName("id")
		val id: Int,
		@field:SerializedName("name")
		val name: String?,
		@field:SerializedName("overview")
		val overView: String?,
		@field:SerializedName("production_code")
		val productionCode: String?,
		@field:SerializedName("season_number")
		val seasonNumber: Int?,
		@field:SerializedName("show_id")
		val showId: Int?,
		@field:SerializedName("still_path")
		val stillPath: String?,
		@field:SerializedName("vote_average")
		val voteAverage: Float?,
		@field:SerializedName("vote_count")
		val voteCount: Int?
): Serializable