package seguindo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicaton.BaseViewModel
import com.google.firebase.database.DataSnapshot
import loading.firebase.ILoadingFireBase
import loading.firebase.LoadingFirebase
import loading.firebase.TypeMediaFireBase.TVSHOW

class FallowModel(application: Application) : BaseViewModel(application) {

	private val loadingFirebase: ILoadingFireBase = LoadingFirebase(TVSHOW)

	private val _fallow: MutableLiveData<DataSnapshot> = MutableLiveData()
	val fallow: LiveData<DataSnapshot> = _fallow

	init {
		fetchFallow()
	}

	private fun fetchFallow() {
		loadingFirebase.allFallow(fallow = _fallow)
	}

}
