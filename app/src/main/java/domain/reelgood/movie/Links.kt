package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
@Keep
class Links: Parcelable {
    @SerializedName("web")
    val web: String? = null // http://movies.netflix.com/Movie/81078819" ("web")
    @SerializedName("ios")
    val ios: String? = null // nflx://www.netflix.com/watch/81078819 ("web")
    @SerializedName("android")
    val android: String? = null // nflx://www.netflix.com/Browse?q=action%3Dplay%26source%3Ddevice%26movieid%3Dhttp%3A%2F%2Fapi-public.netflix.com%2Fcatalog%2Ftitles%2Fmovies%2F81078819 (android)
}