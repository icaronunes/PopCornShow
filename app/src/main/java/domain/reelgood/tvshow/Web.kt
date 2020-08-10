package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Web(
    @SerializedName("episode_id")
    val episodeId: String = "",
    @SerializedName("season_id")
    val seasonId: String = "",
    @SerializedName("show_id")
    val showId: String = ""
) : Parcelable