package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Rank(
    @SerializedName("listing_key")
    val listingKey: ListingKey = ListingKey(),
    @SerializedName("rank")
    val rank: Int = 0,
    @SerializedName("text")
    val text: String = ""
) : Parcelable