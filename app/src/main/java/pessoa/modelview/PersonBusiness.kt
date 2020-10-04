package pessoa.modelview

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.Api

class PersonBusiness(val app: Application, activity: Activity,val personViewModel: PersonViewModel) {

    fun getPersonDate(idPerson: Int) {
        GlobalScope.launch(personViewModel.coroutineContext) {
            val person = Api(app).personDetails(idPerson)
            personViewModel.setPerson(person)
        }
    }
}
