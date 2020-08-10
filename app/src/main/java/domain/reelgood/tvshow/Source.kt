package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Source(
    @SerializedName("access_type")
    val accessType: Int = 0,
    @SerializedName("source_name")
    val sourceName: String = ""
) : Parcelable