package favority

import ID
import Layout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import utils.kotterknife.bindArgument
import utils.patternRecyclerGrid

class ListYourFragment : BaseFragment() {

	val model: YourListViewModel by lazy { createViewModel(YourListViewModel::class.java) }

	private val typeDataRef: String by bindArgument(Constant.MEDIATYPE)
	private val type: String by bindArgument(Constant.ABA)
	lateinit var recyclerViewFilme: RecyclerView

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		return with(inflater.inflate(Layout.temporadas, container, false)) {
			findViewById<View>(ID.progress_temporadas).visibility = View.GONE
			recyclerViewFilme = findViewById(R.id.temporadas_recycler)
			recyclerViewFilme.patternRecyclerGrid(2).apply {
				adapter = YourListAdapter(requireContext(), typeDataRef, type) { id ->
					model.removeMedia(id, type, typeDataRef)
				}
			}
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
				it.ref.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onCancelled(p0: DatabaseError) {}
					override fun onDataChange(snapshot: DataSnapshot) {
						snapshot.children.mapNotNull { child ->
							child.getValue(TvshowDB::class.java)
						}.let { list ->
							(recyclerViewFilme.adapter as YourListAdapter).addList(list)
						}
					}
				})
			})
		} else {
			model.movie.observe(viewLifecycleOwner, Observer {
				it.ref.addListenerForSingleValueEvent(object : ValueEventListener {
					override fun onCancelled(p0: DatabaseError) {}
					override fun onDataChange(snapshot: DataSnapshot) {
						snapshot.children.mapNotNull { child ->
							child.getValue(MovieDb::class.java)
						}.let { list ->
							(recyclerViewFilme.adapter as YourListAdapter).addList(list)
						}
					}
				})
			})
		}
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