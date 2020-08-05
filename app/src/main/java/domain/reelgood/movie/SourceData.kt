package domain.reelgood.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
class SourceData: Parcelable {
    @SerializedName("links")
    val links: Links? = null
    @SerializedName("references")
    val references: References? = null
    @SerializedName("source_name")
    val sourceName: String? = null
    @SerializedName("access_type")
    val accessType: String? = null
}