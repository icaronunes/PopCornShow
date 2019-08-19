package applicaton

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.icaro.filme.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(open val app: Application) : AndroidViewModel(app), LifecycleObserver {

    private val job: Job = Job()
    val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main + job

    val failura = MutableLiveData<BaseView.Failure>()
    val success = MutableLiveData<BaseView.Success>()


    fun ops(){
        Toast.makeText(app.baseContext, app.getString(R.string.ops), Toast.LENGTH_LONG).show()
    }

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    sealed class BaseView {
        object Failure : BaseView()
        object Success : BaseView()
    }
}