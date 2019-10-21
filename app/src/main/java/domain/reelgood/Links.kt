package domain.reelgood

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("web")
    val web: String // https://www.verizon.com/Ondemand/Movies/MovieDetails/movie/TVNX0038959303025930
)