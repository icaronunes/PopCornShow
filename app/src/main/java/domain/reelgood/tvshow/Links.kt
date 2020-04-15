package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Links(
    @SerializedName("android")
    val android: String = "",
    @SerializedName("ios")
    val ios: String = "",
    @SerializedName("web")
    val web: String = ""
) : Parcelable