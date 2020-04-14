package applicaton

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import busca.SearchMultiModelView
import filme.MovieDetatilsViewModel
import utils.Api
import login.LoginViewModel
import main.MainFragViewModel
import main.MainViewModel
import pessoa.modelview.PersonViewModel

class PopCornViewModelFactory constructor(
    private val application: Application,
    private val api: Api = Api(application),
    private val activity: Activity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainFragViewModel::class.java) -> MainFragViewModel(application = application)
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application)
                isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(app = application)
                isAssignableFrom(PersonViewModel::class.java) -> PersonViewModel(app = application, activity = activity)
                isAssignableFrom(SearchMultiModelView::class.java) -> SearchMultiModelView(app = application)
                isAssignableFrom(MovieDetatilsViewModel::class.java) -> MovieDetatilsViewModel(app = application, activity = activity, api = api)
                else -> throw IllegalArgumentException("Class Desconhecida...")
            }
        } as T
}
