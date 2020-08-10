package seguindo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import customview.LoadingShimmer.PaymentLoadingsType
import domain.UserTvshow
import utils.gone
import utils.visible

/**
 * Created by icaro on 25/11/16.
 */
class ListAllFollowFragment(override val layout: Int = Layout.seguindo) : BaseFragment() {

	private lateinit var fallowAdapterDelegates: FallowAdapterDelegates
	private lateinit var recycler: RecyclerView
	private lateinit var sad: ImageView
	private lateinit var empty: TextView

	private val model: FallowModel by lazy { createViewModel(FallowModel::class.java) }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = getViewSeguindo(inflater, container)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observers()
	}

	private fun observers() {

		model.fallow.observe(viewLifecycleOwner, Observer { dataSnapshot ->
			if (dataSnapshot.exists()) {
				try {
					fallowAdapterDelegates.clearList()
					sad.gone()
					empty.gone()
					dataSnapshot.children
						.asSequence()
						.map { shot ->
							shot.getValue(UserTvshow::class.java)
						}
						.forEach {
							fallowAdapterDelegates.add(it!!)
						}
				} catch (e: Exception) {
					sad.visible()
					empty.apply {
						text = getString(R.string.ops)
						visible()
					}
				}
			} else {
				sad.visible()
				empty.apply {
					text = getString(R.string.empty)
					visible()
				}
			}
		})
	}

	private fun getViewSeguindo(inflater: LayoutInflater, container: ViewGroup?): View {
		return inflater.inflate(layout, container, false).apply {
			sad = findViewById(R.id.img_error)
			empty = findViewById(R.id.text_search_empty)
			findViewById<ProgressBar>(R.id.progress_horizontal).apply { gone() }
			fallowAdapterDelegates = FallowAdapterDelegates(context, PaymentLoadingsType.Photo)
			recycler = findViewById<RecyclerView>(R.id.seguindo_recycle).apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager = GridLayoutManager(this@ListAllFollowFragment.context, 4)
				adapter = fallowAdapterDelegates
			}
		}
	}
}
