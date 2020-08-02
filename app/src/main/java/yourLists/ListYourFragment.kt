package yourLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import domain.MovieDb
import domain.TvshowDB
import loading.firebase.TypeDataRef
import loading.firebase.TypeMediaFireBase
import utils.Constant
import utils.gone
import utils.kotterknife.bindArgument
import utils.patternRecyclerGrid
import utils.visible

class ListYourFragment : BaseFragment() {
	val model: YourListViewModel by lazy { createViewModel(YourListViewModel::class.java) }
	private val typeDataRef: String by bindArgument(Constant.MEDIATYPE)
	private val type: String by bindArgument(Constant.ABA)
	private lateinit var empty: TextView
	private lateinit var sad: ImageView
	private lateinit var progress: ProgressBar
	lateinit var recyclerView: RecyclerView
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		return with(inflater.inflate(R.layout.temporadas, container, false)) {
			progress = findViewById(R.id.progress_temporadas)
			recyclerView = findViewById(R.id.temporadas_recycler)
			empty = findViewById(R.id.text_search_empty)
			sad = findViewById(R.id.img_error)
			recyclerView.patternRecyclerGrid(2).apply {
				adapter = YourListAdapter(requireContext(), typeDataRef, type) { id ->
					model.removeMedia(id, type, typeDataRef)
				}
			}
			this
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observers()
		model.fetchDate(typeDataRef)
	}

	private fun observers() {
		if (type == TypeMediaFireBase.TVSHOW.type()) {
			model.tv.observe(viewLifecycleOwner, Observer {
				progress.gone()
				it.ref.addValueEventListener(object : ValueEventListener {
					override fun onCancelled(p0: DatabaseError) {
						error()
					}

					override fun onDataChange(snapshot: DataSnapshot) {
						snapshot.children.mapNotNull { child ->
							child.getValue(TvshowDB::class.java)
						}.let { list ->
							if (list.isNotEmpty()) {
								(recyclerView.adapter as YourListAdapter).addList(list)
								successView()
							} else {
								empty()
							}
						}
					}
				})
			})
		} else {
			model.movie.observe(viewLifecycleOwner, Observer {
				progress.gone()
				it.ref.addValueEventListener(object : ValueEventListener {
					override fun onCancelled(p0: DatabaseError) {
						error()
					}

					override fun onDataChange(snapshot: DataSnapshot) {
						snapshot.children.mapNotNull { child ->
							child.getValue(MovieDb::class.java)
						}.let { list ->
							if (list.isNotEmpty()) {
								(recyclerView.adapter as YourListAdapter).addList(list)
								successView()
							} else {
								empty()
							}
						}
					}
				})
			})
		}
	}

	private fun successView() {
		sad.gone()
		empty.gone()
		recyclerView.visible()
	}

	fun empty() {
		recyclerView.gone()
		empty.apply {
			visible()
			text = context.getString(R.string.empty)
		}
	}

	fun error() {
		empty()
		sad.visibility = View.VISIBLE
	}

	companion object {
		fun newInstance(typeDataRef: TypeDataRef, aba: TypeMediaFireBase): Fragment {
			val fragment = ListYourFragment()
			fragment.arguments = Bundle().apply {
				putString(Constant.MEDIATYPE, typeDataRef.type())
				putString(Constant.ABA, aba.type())
			}
			return fragment
		}
	}
}