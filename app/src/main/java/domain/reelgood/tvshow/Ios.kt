package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Ios(
    @SerializedName("episode_id")
    val episodeId: String = ""
) : Parcelable