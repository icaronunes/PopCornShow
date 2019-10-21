package domain.reelgood

import com.google.gson.annotations.SerializedName

data class Trailer(
    @SerializedName("key")
    val key: String, // QZ40WlshNwU
    @SerializedName("site")
    val site: String, // youtube
    @SerializedName("url")
    val url: Any // null
)