package domain.reelgood.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LinkReferences: Parcelable {
    @SerializedName("movie_id")
    val movieId: String = ""// TVNX0038959303025930
    @SerializedName("show_id")
    val showId: String = ""// TVNX0038959303025930
    @SerializedName("episode_id")
    val epId: String = ""// TVNX0038959303025930
}