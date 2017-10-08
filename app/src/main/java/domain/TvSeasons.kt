package domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class TvSeasons(

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("episodes")
	val episodes: List<EpisodesItem?>? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null
): Serializable

@Generated("com.robohorse.robopojogenerator")
data class EpisodesItem(

		@field:SerializedName("production_code")
		val productionCode: String? = null,

		@field:SerializedName("air_date")
		val airDate: String? = null,

		@field:SerializedName("overview")
		val overview: String? = null,

		@field:SerializedName("episode_number")
		val episodeNumber: Int? = null,

		@field:SerializedName("vote_average")
		val voteAverage: Double? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("season_number")
		val seasonNumber: Int? = null,

		@field:SerializedName("id")
		val id: Int? = null,

		@field:SerializedName("still_path")
		val stillPath: String? = null,

		@field:SerializedName("vote_count")
		val voteCount: Int? = null,

		@field:SerializedName("crew")
		val crew: List<CrewItem?>? = null,

		@field:SerializedName("guest_stars")
		val guestStars: List<GuestStarsItem?>? = null
): Serializable

@Generated("com.robohorse.robopojogenerator")
data class CrewItem(

		@field:SerializedName("gender")
		val gender: Int? = null,

		@field:SerializedName("credit_id")
		val creditId: String? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("profile_path")
		val profilePath: String? = null,

		@field:SerializedName("id")
		val id: Int? = null,

		@field:SerializedName("department")
		val department: String? = null,

		@field:SerializedName("job")
		val job: String? = null
): Serializable

@Generated("com.robohorse.robopojogenerator")
data class GuestStarsItem(

		@field:SerializedName("character")
		val character: String? = null,

		@field:SerializedName("gender")
		val gender: Int? = null,

		@field:SerializedName("credit_id")
		val creditId: String? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("profile_path")
		val profilePath: String? = null,

		@field:SerializedName("id")
		val id: Int? = null,

		@field:SerializedName("order")
		val order: Int? = null
): Serializable