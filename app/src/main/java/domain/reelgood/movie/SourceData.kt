package domain.reelgood.movie

import com.google.gson.annotations.SerializedName

class SourceData {
    @SerializedName("links")
    val links: Links? = null
    @SerializedName("references")
    val references: References? = null
    @SerializedName("source_name")
    val sourceName: String? = null
    @SerializedName("access_type")
    val accessType: String? = null
}