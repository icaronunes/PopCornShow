package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Rank(
    @SerializedName("listing_key")
    val listingKey: ListingKey,
    @SerializedName("rank")
    val rank: Int, // 469
    @SerializedName("text")
    val text: String // Crime
): Parcelable