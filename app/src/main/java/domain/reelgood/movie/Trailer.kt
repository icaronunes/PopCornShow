package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Trailer(
    @SerializedName("key")
    val key: String, // QZ40WlshNwU
    @SerializedName("site")
    val site: String, // youtube
    @SerializedName("url")
    val url: String // null
): Parcelable