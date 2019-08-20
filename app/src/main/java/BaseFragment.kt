import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import applicaton.PopCornApplication
import applicaton.PopCornViewModelFactory
import main.MainFragViewModel

open class BaseFragment : Fragment() {

    fun createViewModel(java: Class<MainFragViewModel>): MainFragViewModel {
        val factory = PopCornViewModelFactory(application = this.activity?.application as PopCornApplication)
        return ViewModelProviders.of(this, factory).get(java)
    }

}
