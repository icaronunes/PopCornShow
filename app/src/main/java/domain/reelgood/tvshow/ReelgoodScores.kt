package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ReelgoodScores(
    @SerializedName("content")
    val content: Double = 0.0,
    @SerializedName("reelgood_rank")
    val reelgoodRank: Int = 0,
    @SerializedName("streamability")
    val streamability: Double = 0.0
) : Parcelable