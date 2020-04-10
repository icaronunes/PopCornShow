package domain.reelgood.movie

import com.google.gson.annotations.SerializedName
import domain.reelgood.movie.Links
import domain.reelgood.movie.References

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