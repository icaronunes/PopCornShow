package applicaton

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import domain.Api
import login.LoginViewModel
import main.MainFragViewModel
import main.MainViewModel
import java.lang.IllegalArgumentException

class PopCornViewModelFactory constructor(private val application: Application,
                                          private val api: Api? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
           when {
                isAssignableFrom(MainFragViewModel::class.java) -> MainFragViewModel(application = application)
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application)
               isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(app = application)
                else -> throw  IllegalArgumentException("Class Desconhecida...")
            }
        } as T
}