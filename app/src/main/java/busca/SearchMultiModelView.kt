package busca

import android.app.Application
import android.os.Handler
import android.os.Looper
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import utils.Api
import kotlin.coroutines.CoroutineContext

class SearchMultiModelView(override val app: Application): BaseViewModel(app) {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, e ->
            Handler(Looper.getMainLooper()).post {
                setErro(e)
            }
        }

    private fun setErro(e: Throwable) { _response.value = Failure<Exception>(Exception(e)) }

    fun fetchData(query: String, pager: Int = 1) {
        GlobalScope.launch(coroutineContext) {
            val response = async { Api(app).getTmdbSearch(query, pager) }
            _response.value = Success(response.await())
        }
    }

    fun setLoading(status: Boolean) {
        _response.value = Loading<Boolean>(status)
    }
}
