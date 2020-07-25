package seguindo

import Layout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.google.firebase.database.DataSnapshot
import customview.LoadingShimmer.PaymentLoadingsType
import domain.UserTvshow
import domain.fistNotWatch
import utils.gone
import utils.visible
import java.util.ArrayList

/**
 * Created by icaro on 25/11/16.
 */
class ListFollowFragment : BaseFragment() {

	private lateinit var recycler: RecyclerView
	private var fallowAdapterDelegates: FallowAdapterDelegates? = null
	private lateinit var sad: ImageView
	private lateinit var empty: TextView

	private val model: FallowModel by lazy { createViewModel(FallowModel::class.java) }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return getViewMissing(inflater, container)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observers()
	}

	private fun observers() {
		model.fallow.observe(viewLifecycleOwner, Observer {
			setDataFallow(it)
		})

		model.update.observe(viewLifecycleOwner, Observer {
			fallowAdapterDelegates?.add(it)
		})
	}

	private fun setDataFallow(dataSnapshot: DataSnapshot) {

		sad.gone()
		empty.gone()
		if (dataSnapshot.exists()) {
			try {
				val userTvshowFire = ArrayList<UserTvshow>()
				sad.gone()
				empty.gone()
				dataSnapshot.children
					.asSequence()
					.map {
						it.getValue(UserTvshow::class.java)
					}
					.forEach { userTvshowFire.add(it!!) }
				val notWatch = userTvshowFire.mapNotNull {
					val ep = it.fistNotWatch()
					if (ep == null) null else Pair(it, ep)
				}
				fallowAdapterDelegates?.clearList()
				model.fetchMedia(notWatch)
			} catch (e: Exception) {
				sad.visible()
				empty.apply {
					text = getString(R.string.ops)
					visible()
				}
				recycler.gone()
			}
		} else {
			recycler.gone()
			sad.visible()
			empty.apply {
				text = getString(R.string.empty)
				visible()
			}
		}
	}

	private fun getViewMissing(inflater: LayoutInflater, container: ViewGroup?): View {
		return with(inflater.inflate(Layout.temporadas, container, false)) {
			fallowAdapterDelegates = FallowAdapterDelegates(context, PaymentLoadingsType.CalendarTv)
			findViewById<ProgressBar>(R.id.progress_temporadas).apply { gone() }
			sad = findViewById(R.id.img_error)
			empty = findViewById(R.id.text_search_empty)
			recycler = findViewById<RecyclerView>(R.id.temporadas_recycler).apply {
				setHasFixedSize(true)
				itemAnimator = DefaultItemAnimator()
				layoutManager = LinearLayoutManager(context)
				adapter = fallowAdapterDelegates
			}
			this
		}
	}
}
