package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Streamability(
    @SerializedName("text")
    val text: String = "",
    @SerializedName("type")
    val type: String = ""
) : Parcelable