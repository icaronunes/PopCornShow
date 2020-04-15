package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class References(
    @SerializedName("android")
    val android: Android = Android(),
    @SerializedName("ios")
    val ios: Ios = Ios(),
    @SerializedName("web")
    val web: Web = Web()
) : Parcelable