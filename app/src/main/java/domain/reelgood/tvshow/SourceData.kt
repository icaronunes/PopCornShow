package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class SourceData(
    @SerializedName("links")
    val links: Links = Links(),
    @SerializedName("references")
    val references: References = References()
) : Parcelable