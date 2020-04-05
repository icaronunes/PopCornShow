package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Rank(
    @SerializedName("listing_key")
    val listingKey: ListingKey = ListingKey(),
    @SerializedName("rank")
    val rank: Int = 0,
    @SerializedName("text")
    val text: String = ""
) : Parcelable