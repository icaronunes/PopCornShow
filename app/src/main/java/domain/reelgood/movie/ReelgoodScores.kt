package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReelgoodScores(
    @SerializedName("content")
    val content: Double, // 76.26
    // @SerializedName("follow_through")
    // val followThrough: Any, // null
    @SerializedName("reelgood_rank")
    val reelgoodRank: Int, // 2128
    @SerializedName("streamability")
    val streamability: Double // 56.98
): Parcelable