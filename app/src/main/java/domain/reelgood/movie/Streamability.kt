package domain.reelgood.movie

import com.google.gson.annotations.SerializedName

data class Streamability(
    @SerializedName("text")
    val text: String, // Not available to stream on a TV everywhere service.
    @SerializedName("type")
    val type: String // con
)