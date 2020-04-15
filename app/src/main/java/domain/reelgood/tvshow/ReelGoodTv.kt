package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReelGoodTv(
    @SerializedName("episode_availability")
    val episodeAvailability: EpisodeAvailability = EpisodeAvailability(),
    @SerializedName("episodes")
    val episodes: HashMap<String, Episode> = HashMap(),
    @SerializedName("genres")
    val genres: List<Int> = listOf(),
    @SerializedName("has_backdrop")
    val hasBackdrop: Boolean = false,
    @SerializedName("has_poster")
    val hasPoster: Boolean = false,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("imdb_rating")
    val imdbRating: Double = 0.0,
    @SerializedName("on_free")
    val onFree: Boolean = false,
    @SerializedName("on_rent_purchase")
    val onRentPurchase: Boolean = false,
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("people")
    val people: List<People> = listOf(),
    @SerializedName("recommended_episode")
    val recommendedEpisode: RecommendedEpisode = RecommendedEpisode(),
    @SerializedName("reelgood_scores")
    val reelgoodScores: ReelgoodScores = ReelgoodScores(),
    @SerializedName("reelgood_synopsis")
    val reelgoodSynopsis: String = "",
    @SerializedName("released_on")
    val releasedOn: String = "",
    @SerializedName("rt_audience_rating")
    val rtAudienceRating: Int = 0,
    @SerializedName("rt_critics_rating")
    val rtCriticsRating: Int = 0,
    @SerializedName("score_breakdown")
    val scoreBreakdown: ScoreBreakdown = ScoreBreakdown(),
    @SerializedName("season_count")
    val seasonCount: Int = 0,
    @SerializedName("seasons")
    val seasons: List<Season> = listOf(),
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("sources")
    val sources: List<String> = listOf(),
    @SerializedName("title")
    val title: String = "",
    @SerializedName("tracking")
    val tracking: Boolean = false,
    @SerializedName("unwatched")
    val unwatched: Int = 0
) : Parcelable