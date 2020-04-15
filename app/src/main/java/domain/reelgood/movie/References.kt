package domain.reelgood.movie

import com.google.gson.annotations.SerializedName

//Todo criar o mesmo objeto para todos
data class References(
    @SerializedName("web")
    val web: LinkReferences?,
    @SerializedName("android")
    val android: LinkReferences,
    @SerializedName("ios")
    val ios: LinkReferences?
    //Todo criar o mesmo objeto para todos
)