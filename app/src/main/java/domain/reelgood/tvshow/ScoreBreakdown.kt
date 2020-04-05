package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class ScoreBreakdown(
    @SerializedName("content")
    val content: Content = Content(),
    @SerializedName("streamability")
    val streamability: List<Streamability> = listOf()
) : Parcelable