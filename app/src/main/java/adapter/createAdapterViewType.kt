package adapter

import androidx.collection.SparseArrayCompat
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import seguindo.FallowAllRecycleAdapter
import seguindo.NextTvAdapter
import utils.Constant

val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>().apply {
	put(Constant.ViewTypesIds.FALLOW, NextTvAdapter())
	put(Constant.ViewTypesIds.TVSHOW, FallowAllRecycleAdapter())
}