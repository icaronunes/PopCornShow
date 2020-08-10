package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Links(
    @SerializedName("android")
    val android: String = "",
    @SerializedName("ios")
    val ios: String = "",
    @SerializedName("web")
    val web: String = ""
) : Parcelable