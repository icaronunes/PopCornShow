package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class ListingKey(
    @SerializedName("listing_identifier")
    val listingIdentifier: String = "",
    @SerializedName("listing_type")
    val listingType: String = ""
) : Parcelable