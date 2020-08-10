package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ListingKey(
    @SerializedName("listing_identifier")
    val listingIdentifier: String, // crime
    @SerializedName("listing_type")
    val listingType: String // genre
): Parcelable