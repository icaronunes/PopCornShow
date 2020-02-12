package domain.search


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class SearchMulti(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("results")
    val results: List<Result> = listOf(),
    @SerializedName("total_pages")
    val totalPages: Int = 0,
    @SerializedName("total_results")
    val totalResults: Int = 0
) : Parcelable