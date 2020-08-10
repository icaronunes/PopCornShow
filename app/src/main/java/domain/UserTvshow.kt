package domain

import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import domain.tvshow.ExternalIds
import kotlinx.android.parcel.Parcelize
import utils.Constant

fun UserTvshow.fistNotWatch(): UserEp? {
	val season = this.seasons.firstOrNull {
		if (it.seasonNumber == 0) {
			false
		} else {
			!it.isVisto
		}
	}
	return season?.userEps?.firstOrNull { !it.isAssistido }
}

/**
 * Created by icaro on 02/11/16.
 */
@Keep
@Parcelize
data class UserTvshow(
	var nome: String? = null,
	var id: Int = 0,
	var numberOfEpisodes: Int = 0,
	var numberOfSeasons: Int = 0,
	var poster: String? = null,
	var seasons: MutableList<UserSeasons> = mutableListOf(),
	var externalIds: ExternalIds? = null,
	var desatualizada: Boolean = false
) : Parcelable, ViewType {
	override fun getViewType() = Constant.ViewTypesIds.TVSHOW
}
