package domain.reelgood

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("also_watched")
    val alsoWatched: List<AlsoWatched>,
    @SerializedName("ranks")
    val ranks: List<Rank>,
    @SerializedName("text")
    val text: String // Salt has an average Rotten Tomatoes (critics) score of 63% and an average IMDb audience rating of 6.4 (280,497 votes). The movie is popular with Reelgood users lately.
)