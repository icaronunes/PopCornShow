package domain.reelgood.movie

import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("display_name")
    val displayName: String, // Political
    @SerializedName("slug")
    val slug: String // political
)