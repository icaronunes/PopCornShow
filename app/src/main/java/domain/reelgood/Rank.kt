package domain.reelgood

import com.google.gson.annotations.SerializedName

data class Rank(
    @SerializedName("listing_key")
    val listingKey: ListingKey,
    @SerializedName("rank")
    val rank: Int, // 469
    @SerializedName("text")
    val text: String // Crime
)