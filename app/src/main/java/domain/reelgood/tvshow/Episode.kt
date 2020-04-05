package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import domain.reelgood.Availability

@Parcelize
data class Episode(
    @SerializedName("aired_at")
    val airedAt: String = "",
    @SerializedName("availability")
    val availability: List<Availability> = listOf(),
    @SerializedName("has_screenshot")
    val hasScreenshot: Boolean = false,
    @SerializedName("number")
    val number: Float = 0f,
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("season_id")
    val seasonId: String = "",
    @SerializedName("sequence_number")
    val sequenceNumber: Double = 0.0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("watched")
    val watched: Boolean = false
) : Parcelable