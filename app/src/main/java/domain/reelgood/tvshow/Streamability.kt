package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Streamability(
    @SerializedName("text")
    val text: String = "",
    @SerializedName("type")
    val type: String = ""
) : Parcelable