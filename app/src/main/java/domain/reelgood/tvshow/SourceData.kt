package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class SourceData(
    @SerializedName("links")
    val links: Links = Links(),
    @SerializedName("references")
    val references: References = References()
) : Parcelable