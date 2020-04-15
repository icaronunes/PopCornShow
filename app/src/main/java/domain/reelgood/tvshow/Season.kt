package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Season(
    @SerializedName("availability")
    val availability: Availability = Availability(),
    @SerializedName("episodes")
    val episodes: List<String> = listOf(),
    @SerializedName("episodes_unwatched")
    val episodesUnwatched: Int = 0,
    @SerializedName("has_poster")
    val hasPoster: Boolean = false,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("number")
    val number: Int = 0
) : Parcelable