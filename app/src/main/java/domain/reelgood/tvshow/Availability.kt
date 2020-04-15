package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Availability(
    @SerializedName("purchase_available")
    val purchaseAvailable: Boolean = false,
    @SerializedName("sources")
    val sources: List<Source> = listOf()
) : Parcelable