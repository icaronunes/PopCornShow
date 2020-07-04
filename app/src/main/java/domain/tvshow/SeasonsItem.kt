package domain.tvshow

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class SeasonsItem(

    @field:SerializedName("air_date")
    val airDate: String? = null,

    @field:SerializedName("episode_count")
    val episodeCount: Int? = null,

    @field:SerializedName("season_number")
    val seasonNumber: Int,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("overview")
    val overview: String? = null

) : Serializable
