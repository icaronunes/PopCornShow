package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Availability(
    @SerializedName("purchase_available")
    val purchaseAvailable: Boolean = false,
    @SerializedName("sources")
    val sources: List<Source> = listOf()
) : Parcelable