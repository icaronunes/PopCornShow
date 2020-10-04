package elenco.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import domain.Credits
import loading.api.ILoadingMedia
import loading.api.LoadingMedia
import utils.Api
import utils.enums.EnumTypeMedia

class WorksViewModel(override val app: Application, api: Api) : BaseViewModel(app = app) {
	val loadingMedia: ILoadingMedia = LoadingMedia(api)

	var _credits: MutableLiveData<BaseRequest<Credits>> = MutableLiveData()
	val credits: LiveData<BaseRequest<Credits>> = _credits

	fun fetchCredits(type: EnumTypeMedia, id: Int, seasonNumer: Int) {
		setLoading(true)
		when(type) {
			EnumTypeMedia.MOVIE -> fetchMovieCredits(id)
			EnumTypeMedia.TV ->  fetchSeasonCredits(id, seasonNumer)
			else -> {}
		}
	}
	private fun fetchSeasonCredits(id: Int, seasonNumer: Int) {
		loadingMedia.fetchSeasonCredits(id, seasonNumer, _credits)
	}

	private fun fetchMovieCredits(id: Int) {
		loadingMedia.fetchMovieCredits(id, _credits)
	}
	fun setLoading(loading: Boolean) {
		_credits.value = Loading(loading)
	}
}
