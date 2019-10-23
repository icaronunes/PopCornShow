package domain.reelgood

import com.google.gson.annotations.SerializedName

data class ScoreBreakdown(
    @SerializedName("content")
    val content: Content,
    @SerializedName("streamability")
    val streamability: List<Streamability>
)