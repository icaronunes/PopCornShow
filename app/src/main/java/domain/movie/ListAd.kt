package domain.movie

import androidx.annotation.Keep
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ViewType
import utils.Constant

@Keep
data class ListAd(val unifiedNativeAd: UnifiedNativeAd): ViewType {
    companion object {
        fun createList(list: List<UnifiedNativeAd>)=  list.map { ListAd(it) }
    }
    override fun getViewType() = Constant.ViewTypesIds.AD
}
