package domain

import android.support.annotation.Keep

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

        var seasons: MutableList<UserSeasons>? = null,

        var externalIds: ExternalIds? = null,

        var desatualizada: Boolean = false
) : Serializable

