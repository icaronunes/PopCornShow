package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AlsoWatched(
    @SerializedName("content_type")
    val contentType: String, // Movie
    @SerializedName("id")
    val id: String, // 566ade1b-7fb8-41eb-8b51-f010c3a246ac
    @SerializedName("slug")
    val slug: String, // inglourious-basterds-2009
    @SerializedName("title")
    val title: String // Inglourious Basterds
): Parcelable