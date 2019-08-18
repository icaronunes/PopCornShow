import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import applicaton.FilmeApplication
import br.com.icaro.filme.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel() {
    private val job: Job = Job()
    val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main + job

    val failura = MutableLiveData<BaseView.Failure>()
    val success = MutableLiveData<BaseView.Success>()


    fun ops(application: FilmeApplication){
        Toast.makeText(application, application.getString(R.string.ops), Toast.LENGTH_LONG).show()
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