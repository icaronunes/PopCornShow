package applicaton

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import br.com.icaro.filme.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(open val app: Application) : AndroidViewModel(app), LifecycleObserver {

    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, _ ->
            Handler(Looper.getMainLooper()).post {
                ops()
            }
        }

    fun ops() {
        Toast.makeText(app.baseContext, app.getString(R.string.ops), Toast.LENGTH_LONG).show()
    }

    fun noInternet() {
        Toast.makeText(app.baseContext, R.string.no_internet, Toast.LENGTH_LONG).show()
    }

    override fun onCleared() {
        if (coroutineContext.isActive) coroutineContext.cancel()
        super.onCleared()
    }

    sealed class BaseRequest<in T> {
        class Failure<T>(val error: Exception) : BaseRequest<T>()
        class Success<T>(val result: T) : BaseRequest<T>()
    }
}
