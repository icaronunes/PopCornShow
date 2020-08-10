package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Availability(
    @SerializedName("purchase_available")
    val purchaseAvailable: Boolean = false,
    @SerializedName("sources")
    val sources: List<Source> = listOf()
) : Parcelable