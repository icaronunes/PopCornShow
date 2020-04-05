package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Android(
    @SerializedName("episode_id")
    val episodeId: String = "",
    @SerializedName("show_id")
    val showId: String = ""
) : Parcelable