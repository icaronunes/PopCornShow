package domain.reelgood

import com.google.gson.annotations.SerializedName

data class References(
    @SerializedName("web")
    val web: Web,
    @SerializedName("android")
    val android: Android

)