package domain.tvshow

import android.os.Parcelable
import domain.EpisodesItem
import domain.UserTvshow
import domain.ViewType
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import utils.Constant

@Parcelize
class Fallow(
	private val first: Tvshow,
	private val second: UserTvshow,
	private val third: EpisodesItem
) : Parcelable, ViewType {
	@IgnoredOnParcel
	val updated: Triple<Tvshow, UserTvshow, EpisodesItem> = Triple(first, second, third)
	override fun getViewType() = Constant.ViewTypesIds.FALLOW
}