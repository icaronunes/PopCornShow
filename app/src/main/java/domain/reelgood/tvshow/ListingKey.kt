package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ListingKey(
    @SerializedName("listing_identifier")
    val listingIdentifier: String = "",
    @SerializedName("listing_type")
    val listingType: String = ""
) : Parcelable