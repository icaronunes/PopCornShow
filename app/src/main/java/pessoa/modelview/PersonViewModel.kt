package pessoa.modelview

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import applicaton.BaseViewModel
import applicaton.BaseViewModel.BaseRequest.*
import domain.person.Person
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pessoa.modelview.listener.PersonBusinessListener
import utils.success
import kotlin.coroutines.CoroutineContext

class PersonViewModel(override val app: Application,activity: Activity): BaseViewModel(app), PersonBusinessListener {

    private val business: PersonBusiness = PersonBusiness(app, activity, this)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, e ->
            Handler(Looper.getMainLooper()).post {
                setError(Exception(e))
            }
        }

    override fun getPersonDate(idPerson: Int) = business.getPersonDate(idPerson)
    override fun setPerson(person: BaseRequest<Person>) { _response.value = person }
    override fun setError(e: Exception) { _response.value = BaseRequest.Failure<Exception>(e) }
}