package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Source(
    @SerializedName("access_type")
    val accessType: Int = 0,
    @SerializedName("source_name")
    val sourceName: String = ""
) : Parcelable