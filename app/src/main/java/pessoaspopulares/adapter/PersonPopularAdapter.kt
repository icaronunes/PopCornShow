package pessoaspopulares.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import domain.PersonItem
import domain.ViewType
import java.util.ArrayList
import utils.Constant

/**
 * Created by icaro on 04/10/16.
 */
class PersonPopularAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var personResultsPage = ArrayList<ViewType>()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType() = Constant.BuscaConstants.LOADING
    }

    init {
        delegateAdapters.put(Constant.BuscaConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constant.BuscaConstants.NEWS, PersonDelegateAdapter())
        personResultsPage = ArrayList()
        personResultsPage.add(loadingItem)
    }

    fun addPersonPopular(personResults: List<PersonItem>) {
        val initPosition = personResultsPage.size - 1
        this.personResultsPage.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        for (person in personResults) {
            this.personResultsPage.add(person)
        }

        notifyItemRangeChanged(initPosition, this.personResultsPage.size + 1)
        personResultsPage.add(loadingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
        = delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
        = delegateAdapters.get(getItemViewType(position))!!
        .onBindViewHolder(holder, personResultsPage[position], context = null)

    override fun getItemViewType(position: Int): Int = personResultsPage[position].getViewType()
    override fun getItemCount(): Int = personResultsPage.size
}
