package applicaton

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import busca.SearchMultiModelView
import episodio.EpsodioViewModel
import filme.MovieDetatilsViewModel
import login.LoginViewModel
import main.MainViewModel
import pessoa.modelview.PersonViewModel
import seguindo.FallowModel
import tvshow.viewmodel.TvShowViewModel
import utils.Api
import yourLists.YourListViewModel

class PopCornViewModelFactory constructor(
	private val application: Application,
	private val api: Api = Api(application),
	private val activity: Activity
) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		with(modelClass) {
			when {
				isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application, api)
				isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(app = application)
				isAssignableFrom(PersonViewModel::class.java) -> PersonViewModel(
					app = application,
					activity = activity
				)
				isAssignableFrom(SearchMultiModelView::class.java) -> SearchMultiModelView(app = application)
				isAssignableFrom(MovieDetatilsViewModel::class.java) -> MovieDetatilsViewModel(
					app = application,
					api = api
				)
				isAssignableFrom(TvShowViewModel::class.java) -> TvShowViewModel(
					app = application,
					api = api
				)
				isAssignableFrom(FallowModel::class.java) -> FallowModel(
					application = application,
					activity = activity
				)
				isAssignableFrom(YourListViewModel::class.java) -> YourListViewModel(app = application)
				isAssignableFrom(EpsodioViewModel::class.java) -> EpsodioViewModel(app = application)
				else -> throw IllegalArgumentException("Class Desconhecida...")
			}
		} as T
}
