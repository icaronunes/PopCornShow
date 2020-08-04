package domain.tvshow

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Keep
@Generated("com.robohorse.robopojogenerator")
data class ExternalIds(

    @field:SerializedName("imdb_id")
    var imdbId: String? = null,

    @field:SerializedName("freebase_mid")
    var freebaseMid: String? = null,

    @field:SerializedName("tvdb_id")
    var tvdbId: String? = null,

    @field:SerializedName("freebase_id")
    var freebaseId: String? = null,

    @field:SerializedName("tvrage_id")
    var tvrageId: String? = null

) : Serializable
