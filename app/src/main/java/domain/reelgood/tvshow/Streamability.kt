package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Streamability(
    @SerializedName("text")
    val text: String = "",
    @SerializedName("type")
    val type: String = ""
) : Parcelable