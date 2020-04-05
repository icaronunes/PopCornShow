package domain.reelgood.tvshow


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Ios(
    @SerializedName("episode_id")
    val episodeId: String = ""
) : Parcelable