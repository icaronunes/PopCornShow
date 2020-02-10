package fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import applicaton.BaseFragment
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.fragment_error.view.try_again
import utils.CallBackError

class FragmentError : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = with(inflater.inflate(R.layout.fragment_error, container, false)) {
        super.onCreateView(inflater, container, savedInstanceState)
        require(requireActivity() is CallBackError) { Log.e(this.javaClass.name, "Error - Para usar essa class, sua activity precisa ser uma CallBackError") }

        try_again.setOnClickListener { (requireActivity() as? CallBackError)?.tryAgain() }
        this
    }
}