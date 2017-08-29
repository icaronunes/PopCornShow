package pessoaspopulares

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import domain.ViewType

/**
 * Created by icaro on 27/08/17.
 */
interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?)
}