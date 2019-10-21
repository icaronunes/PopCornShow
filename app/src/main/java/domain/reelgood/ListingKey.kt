package domain.reelgood

import com.google.gson.annotations.SerializedName

data class ListingKey(
    @SerializedName("listing_identifier")
    val listingIdentifier: String, // crime
    @SerializedName("listing_type")
    val listingType: String // genre
)