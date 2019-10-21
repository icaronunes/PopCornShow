package domain.reelgood

import com.google.gson.annotations.SerializedName

data class SourceData(
    @SerializedName("links")
    val links: Links,
    @SerializedName("references")
    val references: References
)