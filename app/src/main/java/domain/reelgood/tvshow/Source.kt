package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Source(
    @SerializedName("access_type")
    val accessType: Int = 0,
    @SerializedName("source_name")
    val sourceName: String = ""
) : Parcelable