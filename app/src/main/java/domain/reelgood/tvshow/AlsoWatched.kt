package domain.reelgood.tvshow


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlsoWatched(
    @SerializedName("content_type")
    val contentType: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("title")
    val title: String = ""
) : Parcelable