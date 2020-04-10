package customview.stream.adapter

import androidx.collection.SparseArrayCompat
import customview.stream.TypeEnumStream
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

class AdapterListStreams(val subscription: Boolean, val purchase: Boolean = false, val titleMedia: String = "", val type: TypeEnumStream) {

    private val delegateAdapters: SparseArrayCompat<ViewTypeDelegateAdapter>
        get() = SparseArrayCompat<ViewTypeDelegateAdapter>().apply {
            put(Constant.ReelGood.LOADING, LoadingDelegateAdapter())
            put(Constant.ReelGood.NETFLIX, StreamAbMovieNetflixAdapter(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.HBO, StreamAbMovieHboAdapter(subscription, purchase, type))
            put(Constant.ReelGood.HULU, MovieHuluAdapterStreamAb(subscription, purchase, "", type))
            put(Constant.ReelGood.STARZ, StreamAbMovieStarzAdapter(subscription, purchase, "", type))
            put(Constant.ReelGood.GOOGLEPLAY, StreamAbGoogleAdapter(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.AMAZON, StreamMovieAmazonAdapter(subscription, purchase, type))
            put(Constant.ReelGood.WEB, StreamAbMovieGenericWebAdapter(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.ADULT_SWIM, AdultAdapterStreamAb(subscription, purchase, titleMedia, type))
            put(Constant.ReelGood.FUBO, FuboAdapterStreamAb(subscription, purchase, titleMedia, type))
        }

    fun getDeleteAdapters() = delegateAdapters
}
