package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Links(
    @SerializedName("android")
    val android: String = "",
    @SerializedName("ios")
    val ios: String = "",
    @SerializedName("web")
    val web: String = ""
) : Parcelable