package domain.tvshow

import android.os.Parcelable
import domain.EpisodesItem
import domain.UserTvshow
import domain.ViewType
import kotlinx.android.parcel.Parcelize

@Parcelize
class Fallow(
	private val first: Tvshow,
	private val second: UserTvshow,
	private val third: EpisodesItem
) : Parcelable, ViewType {
	val updated: Triple<Tvshow, UserTvshow, EpisodesItem> = Triple(first, second, third)
	override fun getViewType() = 1
}