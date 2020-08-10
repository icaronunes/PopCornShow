package domain.reelgood.movie

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class People(
    @SerializedName("birthdate")
    val birthdate: String, // 1951-06-09T00:00:00
    @SerializedName("has_poster")
    val hasPoster: Boolean, // true
    @SerializedName("has_square")
    val hasSquare: Boolean, // true
    @SerializedName("id")
    val id: String, // efe2bc04-c497-45b2-ab1b-344ac332779b
    @SerializedName("name")
    val name: String, // James Newton Howard
    @SerializedName("rank")
    val rank: Int, // 15
    @SerializedName("role")
    val role: String, // CIA Security Officer
    @SerializedName("role_type")
    val roleType: Int, // 5
    @SerializedName("slug")
    val slug: String // james-newton-howard-1951
): Parcelable