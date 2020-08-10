package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Android(
    @SerializedName("episode_id")
    val episodeId: String = "",
    @SerializedName("show_id")
    val showId: String = ""
) : Parcelable