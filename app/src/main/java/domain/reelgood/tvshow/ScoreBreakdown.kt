package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ScoreBreakdown(
    @SerializedName("content")
    val content: Content = Content(),
    @SerializedName("streamability")
    val streamability: List<Streamability> = listOf()
) : Parcelable