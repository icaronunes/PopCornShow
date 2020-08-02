package episodio

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import com.google.firebase.database.DataSnapshot
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.TypeMediaFireBase.*

class EpsodioViewModel(override val app: Application) : BaseViewModel(app = app) {
	private val loadingFirebase: ILoadingFireBase = LoadingFirebase(TVSHOW)
	private val _seasons: MutableLiveData<DataSnapshot> = MutableLiveData()
	val seasons: LiveData<DataSnapshot> = _seasons
	fun getSeasonFire(idTvshow: Int, seasonNumber: Int) {
		loadingFirebase.fillSeasons(idTvshow, seasonNumber, _seasons)
	}

	fun watchEp(childUpdates: HashMap<String, Any>) {
		loadingFirebase.wathEp(childUpdates)
	}
}