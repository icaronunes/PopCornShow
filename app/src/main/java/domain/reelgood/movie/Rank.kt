package domain.reelgood.movie

import com.google.gson.annotations.SerializedName
import domain.reelgood.movie.ListingKey

data class Rank(
    @SerializedName("listing_key")
    val listingKey: ListingKey,
    @SerializedName("rank")
    val rank: Int, // 469
    @SerializedName("text")
    val text: String // Crime
)