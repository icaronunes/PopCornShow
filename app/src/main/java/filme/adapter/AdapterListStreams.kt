package filme.adapter

import androidx.collection.SparseArrayCompat
import customview.TypeEnumStream
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

class AdapterListStreams(val subscription: Boolean, val purchase: Boolean = false, val titleMedia: String = "", val type: TypeEnumStream) {

    private val delegateAdapters: SparseArrayCompat<ViewTypeDelegateAdapter>
        get() = SparseArrayCompat<ViewTypeDelegateAdapter>().apply {
            put(Constant.ReelGood.LOADING, LoadingDelegateAdapter())
            put(Constant.ReelGood.NETFLIX, StreamMovieNetflixAdapter(subscription, purchase,titleMedia, type))
            put(Constant.ReelGood.HBO, StreamMovieHboAdapter(subscription, purchase, type))
            put(Constant.ReelGood.HULU, MovieHuluAdapterStream(subscription, purchase, "", type))
            put(Constant.ReelGood.STARZ, StreamMovieStarzAdapter(subscription, purchase, "", type))
            put(Constant.ReelGood.GOOGLEPLAY, StreamGoogleAdapter(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.AMAZON, StreamMovieAmazonAdapter(subscription, purchase, type))
            put(Constant.ReelGood.WEB, StreamMovieGenericWebAdapter(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.ADULT_SWIM, AdultAdapterStream(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.FUBO, FuboAdapterStream(subscription, purchase, titleMedia, type))
        }

    fun getDeleteAdapters() = delegateAdapters
}
