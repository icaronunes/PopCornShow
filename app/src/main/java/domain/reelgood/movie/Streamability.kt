package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Streamability(
    @SerializedName("text")
    val text: String, // Not available to stream on a TV everywhere service.
    @SerializedName("type")
    val type: String // con
): Parcelable