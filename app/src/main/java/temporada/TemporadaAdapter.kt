package temporada

import android.view.View
import domain.EpisodesItem
import domain.UserEp

interface TemporadaOnClickListener {
	fun onClickVerTemporada(status: Boolean, id: Int)
	fun onClickTemporada(position: Int)
	fun onClickTemporadaNota(
		view: View?,
		ep: EpisodesItem,
		position: Int,
		userEp: UserEp?,
		notifyItemChanged: () -> Unit
	)

	fun onClickScrool(position: Int)
}