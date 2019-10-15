package domain.tvshow

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Tvshow(

    @field:SerializedName("original_language")
    val originalLanguage: String? = null,

    @field:SerializedName("number_of_episodes")
    val numberOfEpisodes: Int? = null,

    @field:SerializedName("videos")
    val videos: Videos? = null,

    @field:SerializedName("networks")
    val networks: List<NetworksItem?>? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @field:SerializedName("credits")
    val credits: domain.Credits? = null,

    @field:SerializedName("genres")
    val genres: List<GenresItem?>? = null,

    @field:SerializedName("popularity")
    val popularity: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("number_of_seasons")
    val numberOfSeasons: Int? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null,

    @field:SerializedName("first_air_date")
    val firstAirDate: String? = null,

    @field:SerializedName("overview")
    val overview: String? = null,

    @field:SerializedName("similar")
    val similar: Similar? = null,

    @field:SerializedName("seasons")
    val seasons: List<SeasonsItem?>? = null,

    @field:SerializedName("images")
    val images: Images? = null,

    @field:SerializedName("languages")
    val languages: List<String?>? = null,

    @field:SerializedName("created_by")
    val createdBy: List<CreatedByItem?>? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,

    @field:SerializedName("origin_country")
    val originCountry: List<String?>? = null,

    @field:SerializedName("production_companies")
    val productionCompanies: List<ProductionCompaniesItem?>? = null,

    @field:SerializedName("original_name")
    val originalName: String? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("episode_run_time")
    val episodeRunTime: List<Int?>? = null,

    @field:SerializedName("in_production")
    val inProduction: Boolean? = null,

    @field:SerializedName("last_air_date")
    val lastAirDate: String? = null,

    @field:SerializedName("homepage")
    val homepage: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("external_ids")
    val external_ids: ExternalIds? = null,

    @field:SerializedName("last_episode_to_air")
    val lastEpisodeAir: LastEpisodeAir
) : Serializable
