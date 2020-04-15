package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendedEpisode(
    @SerializedName("episode_id")
    val episodeId: String = "",
    @SerializedName("season_id")
    val seasonId: String = ""
) : Parcelable