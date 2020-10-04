package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReelGoodMovie(
    @SerializedName("availability")
    val availability: List<Availability>

/*    @SerializedName("classification")
//    val classification: String?, // 13+
//    @SerializedName("countries")
//    val countries: List<String>?,
//    @SerializedName("genres")
//    val genres: List<Int>?,
//    @SerializedName("has_backdrop")
//    val hasBackdrop: Boolean = false, // true
//    @SerializedName("has_poster")
//    val hasPoster: Boolean = false, // true
//    @SerializedName("id")
//    val id: String?, // d6866d46-4678-4d66-9031-854301ac71bd
//    @SerializedName("imdb_rating")
//    val imdbRating: Double, // 6.4
//    @SerializedName("language")
//    val language: String?, // en
//    @SerializedName("on_free")
//    val onFree: Boolean, // false
//    @SerializedName("on_rent_purchase")
//    val onRentPurchase: Boolean, // true
//    @SerializedName("overview")
//    val overview: String?, // As a CIA officer, Evelyn Salt swore an oath to duty, honor and country. Her loyalty will be tested when a defector accuses her of being a Russian spy. Salt goes on the run, using all her skills and years of experience as a covert operative to elude capture. Salt's efforts to prove her innocence only serve to cast doubt on her motives, as the hunt to uncover the truth behind her identity continues and the question remains: "Who is Salt?"
//    @SerializedName("people")
//    val people: List<People>?,
//    @SerializedName("reelgood_scores")
//    val reelgoodScores: ReelgoodScores?,
//    @SerializedName("reelgood_synopsis")
//    val reelgoodSynopsis: String?, // Salt featuring Angelina Jolie and Liev Schreiber is streaming with subscription on STARZ, available for rent or purchase on iTunes, available for purchase on Google Play, and 5 others. It's an action & adventure and crime movie with an average Rotten Tomatoes (critics) score of 63% and an average IMDb audience rating of 6.4 (280,497 votes).
//    @SerializedName("released_on")
//    val releasedOn: String?, // 2010-07-21T00:00:00
//    @SerializedName("rt_audience_rating")
//    val rtAudienceRating: Int = 0, // 59
//    @SerializedName("rt_critics_rating")
//    val rtCriticsRating: Int = 0, // 63
//    @SerializedName("runtime")
//    val runtime: Int = 0, // 100
//    @SerializedName("score_breakdown")
//    val scoreBreakdown: ScoreBreakdown?,
//    @SerializedName("seen")
//    val seen: Boolean, // false
//    @SerializedName("slug")
//    val slug: String, // salt-2010
//    @SerializedName("sources")
//    val sources: List<String>,
//    @SerializedName("tagline")
//    val tagline: String, // Who is Salt?
//    @SerializedName("tags")
//    val tags: List<Tag>,
//    @SerializedName("title")
//    val title: String, // Salt
//    @SerializedName("trailer")
//    val trailer: Trailer,
//    @SerializedName("watchlisted")
//    val watchlisted: Boolean // false */
): Parcelable