package temporada

import domain.EpisodesItem
import domain.UserEp

interface SeasonOnClickListener {
	fun onClickVerTemporada(status: Boolean, id: Int)
	fun onClickTemporada(position: Int)
	fun onClickSeasonReated(
		ep: EpisodesItem,
		position: Int,
		userEp: UserEp?,
		notifyItemChanged: () -> Unit
	)

	fun onClickScrool(position: Int)
}