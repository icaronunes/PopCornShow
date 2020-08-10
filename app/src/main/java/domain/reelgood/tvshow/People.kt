package domain.reelgood.tvshow


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class People(
    @SerializedName("birthdate")
    val birthdate: String = "",
    @SerializedName("has_poster")
    val hasPoster: Boolean = false,
    @SerializedName("has_square")
    val hasSquare: Boolean = false,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("rank")
    val rank: Int? = 0,
    @SerializedName("role")
    val role: String? = "",
    @SerializedName("role_type")
    val roleType: Int = 0,
    @SerializedName("slug")
    val slug: String = ""
) : Parcelable