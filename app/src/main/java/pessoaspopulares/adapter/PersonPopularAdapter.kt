package pessoaspopulares.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import domain.PersonItem
import domain.ViewType
import utils.Constantes
import java.util.*

/**
 * Created by icaro on 04/10/16.
 */
class PersonPopularAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    private var personResultsPage = ArrayList<ViewType>()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType() = Constantes.BuscaConstants.LOADING
    }

    init {
        delegateAdapters.put(Constantes.BuscaConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constantes.BuscaConstants.NEWS, PersonDelegateAdapter())
        personResultsPage = ArrayList()
        personResultsPage.add(loadingItem)
    }

    fun addPersonPopular(personResults: List<PersonItem?>?) {

        val initPosition = personResultsPage?.size!! - 1
        this.personResultsPage?.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        // insert news and the loading at the end of the list
        for (person in personResults!!) {
            this.personResultsPage.add(person!!)
        }

        notifyItemRangeChanged(initPosition, this.personResultsPage?.size!! + 1 /* plus loading item */)
        personResultsPage.add(loadingItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegateAdapters.get(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) =
            delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder!!, personResultsPage[position], context = null)

    override fun getItemViewType(position: Int): Int = personResultsPage[position].getViewType()

    override fun getItemCount(): Int = personResultsPage.size
}



