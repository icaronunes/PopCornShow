package domain

import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.adapter_similares.view.similares_date_avaliable
import kotlinx.android.synthetic.main.adapter_similares.view.similares_name
import kotlinx.android.synthetic.main.adapter_similares.view.similares_rated
import kotlinx.android.synthetic.main.adapter_similares.view.similares_title_original
import similares.SimilaresInfo
import utils.Constant
import utils.parseDateShot
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ResultsSimilarItem(

    @field:SerializedName("overview")
    val overview: String? = null,

    @field:SerializedName("original_language")
    val originalLanguage: String? = null,

    @field:SerializedName("original_title")
    val originalTitle: String? = null,

    @field:SerializedName("video")
    val video: Boolean? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("genre_ids")
    val genreIds: List<Int?>? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,

    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @field:SerializedName("release_date")
    val releaseDate: String? = null,

    @field:SerializedName("popularity")
    val popularity: Double? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("adult")
    val adult: Boolean? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null
) : Serializable, SimilaresInfo {
    override fun title() = title ?: ""
    override fun firstDate() = releaseDate ?: ""
    override fun originalTitle() = originalTitle ?: ""
    override fun votes() = voteAverage ?: 0.0
    override fun poster() = posterPath ?: ""
    override fun id() = id
}
