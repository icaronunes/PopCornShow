package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ScoreBreakdown(
    @SerializedName("content")
    val content: Content,
    @SerializedName("streamability")
    val streamability: List<Streamability>
): Parcelable