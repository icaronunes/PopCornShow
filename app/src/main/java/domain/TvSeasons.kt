package domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

fun TvSeasons.fillAllUserEpTvshow(userTvshow: UserSeasons?, status: Boolean): UserSeasons {
	fun findEp(episodes: EpisodesItem, userTvshow: UserSeasons?): UserEp? {
		if (userTvshow == null || userTvshow.userEps.isNullOrEmpty()) return null
		return userTvshow.userEps.find { it.id == episodes.id }?.apply {
			isAssistido = status
		}
	}

	val userEps = this.episodes.map {
		findEp(it, userTvshow) ?: UserEp(
			it.id,
			it.seasonNumber,
			it.episodeNumber!!,
			status,
			0.0f,
			it.name,
			it.airDate
		)
	}.toMutableList()

	return UserSeasons(
		id = id!!,
		seasonNumber = seasonNumber!!,
		isVisto = status,
		userEps = userEps
	)
}

fun TvSeasons.fillEpUserTvshow(userTvshow: UserSeasons?, status: Boolean, idEp: Int): UserSeasons {
	fun findEp(episodes: EpisodesItem, userTvshow: UserSeasons?): UserEp? {
		if (userTvshow == null || userTvshow.userEps.isNullOrEmpty()) return null
		return userTvshow.userEps.find { it.id == episodes.id }?.apply {
			dataEstreia = episodes.airDate
			title = episodes.name
		}
	} // Adicionar mecanismo para assistir anteriores

	val allEps = episodes.map {
		findEp(it, userTvshow) ?: UserEp(
			it.id,
			it.seasonNumber,
			it.episodeNumber,
			false,
			0.0f,
			it.name,
			it.airDate
		)
	}.map {
		if (it.id == idEp) it.isAssistido = status
		it
	}.toMutableList()

	return UserSeasons(id = id!!, seasonNumber = seasonNumber!!, isVisto = status, userEps = allEps)
}

@Generated("com.robohorse.robopojogenerator")
@Keep
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
	val episodes: List<EpisodesItem> = listOf(),
	@field:SerializedName("poster_path")
	val posterPath: String? = null
) : Serializable

fun EpisodesItem.createUserEp(): UserEp {
	return UserEp(
		id,
		seasonNumber,
		episodeNumber ?: -1,
		true,
		0.0f,
		name,
		airDate
	)
}

@Generated("com.robohorse.robopojogenerator")
@Keep
data class EpisodesItem(
	@field:SerializedName("production_code")
	val productionCode: String? = null,
	@field:SerializedName("air_date")
	val airDate: String? = null,
	@field:SerializedName("overview")
	val overview: String? = null,
	@field:SerializedName("episode_number")
	val episodeNumber: Int,
	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,
	@field:SerializedName("name")
	val name: String? = null,
	@field:SerializedName("season_number")
	val seasonNumber: Int,
	@field:SerializedName("id")
	val id: Int,
	@field:SerializedName("still_path")
	val stillPath: String? = null,
	@field:SerializedName("vote_count")
	val voteCount: Int? = null,
	@field:SerializedName("crew")
	val crew: List<CrewItem?>? = null,
	@field:SerializedName("guest_stars")
	val guestStars: List<GuestStarsItem?>? = null
) : Serializable

@Generated("com.robohorse.robopojogenerator")
@Keep
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
) : Serializable

@Generated("com.robohorse.robopojogenerator")
@Keep
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
) : Serializable
