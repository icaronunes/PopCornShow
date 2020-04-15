package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    @SerializedName("also_watched")
    val alsoWatched: List<AlsoWatched> = listOf(),
    @SerializedName("ranks")
    val ranks: List<Rank> = listOf(),
    @SerializedName("text")
    val text: String = ""
) : Parcelable