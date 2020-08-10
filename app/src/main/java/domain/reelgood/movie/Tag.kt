package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Tag(
    @SerializedName("display_name")
    val displayName: String, // Political
    @SerializedName("slug")
    val slug: String // political
): Parcelable