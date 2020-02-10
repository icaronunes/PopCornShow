package pessoa.modelview.listener

import applicaton.BaseViewModel.BaseRequest
import domain.person.Person
import java.lang.Exception

interface PersonBusinessListener {
    fun getPersonDate(idPerson: Int)
    fun setPerson(person: BaseRequest<Person>)
    fun setError(e: Exception)
}
