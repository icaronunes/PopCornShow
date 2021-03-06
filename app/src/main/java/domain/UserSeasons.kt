package domain

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Created by icaro on 03/11/16.
 */
@Keep
data class UserSeasons(
    var userEps: MutableList<UserEp> = mutableListOf(),
    var id: Int = 0,
    var seasonNumber: Int = 0,
    var isVisto: Boolean = false
) : Serializable
