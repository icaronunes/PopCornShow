package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListingKey(
    @SerializedName("listing_identifier")
    val listingIdentifier: String = "",
    @SerializedName("listing_type")
    val listingType: String = ""
) : Parcelable