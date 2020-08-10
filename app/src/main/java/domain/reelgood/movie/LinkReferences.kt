package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
class LinkReferences: Parcelable {
    @SerializedName("movie_id")
    val movieId: String = ""// TVNX0038959303025930
    @SerializedName("show_id")
    val showId: String = ""// TVNX0038959303025930
    @SerializedName("episode_id")
    val epId: String = ""// TVNX0038959303025930
}