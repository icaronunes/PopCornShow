package applicaton

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import busca.SearchMultiModelView
import elenco.viewmodel.WorksViewModel
import episodio.EpsodioViewModel
import filme.MovieDetatilsViewModel
import lista.viewmodel.ListByTypeViewModel
import login.LoginViewModel
import main.MainViewModel
import pessoa.modelview.PersonViewModel
import produtora.viewmodel.ProductionViewModel
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
				isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application, api, activity)
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
				isAssignableFrom(ListByTypeViewModel::class.java) -> ListByTypeViewModel(app = application, api = api)
				isAssignableFrom(EpsodioViewModel::class.java) -> EpsodioViewModel(app = application)
				isAssignableFrom(WorksViewModel::class.java) -> WorksViewModel(app = application, api = api)
				isAssignableFrom(ProductionViewModel::class.java) -> ProductionViewModel(app = application, api = api)
				else -> throw IllegalArgumentException("ViewModel Desconhecida...")
			}
		} as T
}
