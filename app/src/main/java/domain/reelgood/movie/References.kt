package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//Todo criar o mesmo objeto para todos
@Parcelize
@Keep
data class References(
    @SerializedName("web")
    val web: LinkReferences? = null,
    @SerializedName("android")
    val android: LinkReferences? = null,
    @SerializedName("ios")
    val ios: LinkReferences? = null

): Parcelable