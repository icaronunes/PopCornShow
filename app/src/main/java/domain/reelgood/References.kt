package domain.reelgood

import com.google.gson.annotations.SerializedName

//Todo criar o mesmo objeto para todos
data class References(
    @SerializedName("web")
    val web: Web?,
    @SerializedName("android")
    val android: Android?,
    @SerializedName("ios")
    val ios: Web?
    //Todo criar o mesmo objeto para todos
)