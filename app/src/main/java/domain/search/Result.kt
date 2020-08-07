package domain.search


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import domain.ViewType
import kotlinx.android.parcel.Parcelize
import utils.enums.EnumTypeMedia

@Parcelize
data class Result(
    @SerializedName("adult")
    val adult: Boolean = false,
    @SerializedName("backdrop_path")
    val backdropPath: String? = "",
    @SerializedName("first_air_date")
    val firstAirDate: String = "",
    @SerializedName("genre_ids")
    val genreIds: List<String> = listOf(),
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("known_for")
    val knownFor: List<KnownFor> = listOf(),
    @SerializedName("media_type")
    val mediaType: String = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("origin_country")
    val originCountry: List<String> = listOf(),
    @SerializedName("original_language")
    val originalLanguage: String = "",
    @SerializedName("original_name")
    val originalName: String = "",
    @SerializedName("original_title")
    val originalTitle: String = "",
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("popularity")
    val popularity: Double = 0.0,
    @SerializedName("poster_path")
    val posterPath: String? = "",
    @SerializedName("profile_path")
    val profilePath: String? = "",
    @SerializedName("release_date")
    val releaseDate: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("video")
    val video: Boolean = false,
    @SerializedName("vote_average")
    val voteAverage: Float = 0f,
    @SerializedName("vote_count")
    val voteCount: Int = 0
) : Parcelable, ViewType {
    override fun getViewType(): Int {
        return when(mediaType) {
            EnumTypeMedia.MOVIE.type -> 1
            EnumTypeMedia.TV.type -> 2
            EnumTypeMedia.PERSON.type -> 3
            else -> 0
        }
    }
}