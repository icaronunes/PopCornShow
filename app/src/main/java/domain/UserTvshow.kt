package domain

import androidx.annotation.Keep
import domain.tvshow.ExternalIds
import java.io.Serializable

/**
 * Created by icaro on 02/11/16.
 */
@Keep
data class UserTvshow(

    var nome: String? = null,

    var id: Int = 0,

    var numberOfEpisodes: Int = 0,

    var numberOfSeasons: Int = 0,

    var poster: String? = null,

    var seasons: MutableList<UserSeasons> = mutableListOf(),

    var externalIds: ExternalIds? = null,

    var desatualizada: Boolean = false
) : Serializable
