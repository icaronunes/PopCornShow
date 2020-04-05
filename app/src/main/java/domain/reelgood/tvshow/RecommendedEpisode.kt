package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class RecommendedEpisode(
    @SerializedName("episode_id")
    val episodeId: String = "",
    @SerializedName("season_id")
    val seasonId: String = ""
) : Parcelable