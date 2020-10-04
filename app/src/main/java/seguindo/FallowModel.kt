package seguindo

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import com.google.firebase.database.DataSnapshot
import domain.EpisodesItem
import domain.UserEp
import domain.UserTvshow
import domain.tvshow.Fallow
import domain.tvshow.Tvshow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.TypeMediaFireBase.*
import utils.Api

class FallowModel(application: Application, val activity: Activity) : BaseViewModel(application) {

	private val loadingFirebase: ILoadingFireBase = LoadingFirebase(TVSHOW)

	private var listTvShow: MutableList<Tvshow> = mutableListOf()
	private var listEp: MutableList<EpisodesItem> = mutableListOf()

	private val _fallow: MutableLiveData<DataSnapshot> = MutableLiveData()
	val fallow: LiveData<DataSnapshot> = _fallow
	private val _update: MutableLiveData<Fallow> = MutableLiveData()
	val update: LiveData<Fallow> = _update

	init {
		fetchFallow()
	}

	private fun fetchFallow() {
		loadingFirebase.allFallow(fallow = _fallow)
	}

	@Suppress("RedundantAsync")
	fun fetchMedia(notWatchs: List<Pair<UserTvshow, UserEp>>) {
		notWatchs.forEach { pair ->
			val (tvShow, epUser) = pair
			GlobalScope.launch(coroutineContext) {
				val tvShowUpdated =
					listTvShow.find { it.id == tvShow.id } ?: async(Dispatchers.IO) {
						Api(context = activity).getTvShowLiteC(tvShow.id)
					}.await()
				listTvShow.add(tvShowUpdated)
				val ep = listEp.find { it.id == epUser.id } ?: async(Dispatchers.IO) {
					Api(context = activity).getTvShowEpC(
						tvShow.id,
						epUser.seasonNumber,
						epUser.episodeNumber
					)
				}.await()
				listEp.add(ep)
				val updater = Fallow(tvShowUpdated, tvShow, ep)
				_update.value = updater
			}
		}
	}

}
