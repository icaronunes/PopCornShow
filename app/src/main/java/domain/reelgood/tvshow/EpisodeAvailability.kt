package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class EpisodeAvailability(
    @SerializedName("available_elsewhere")
    val availableElsewhere: List<String> = listOf(),
    @SerializedName("free_on_sources")
    val freeOnSources: List<String> = listOf()
//    ,
//    @SerializedName("on_free")
//    val onFree: List<String> = listOf()
) : Parcelable