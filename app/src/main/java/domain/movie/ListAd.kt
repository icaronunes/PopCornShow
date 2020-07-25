package domain.movie

import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ViewType
import utils.Constant

data class ListAd(val unifiedNativeAd: UnifiedNativeAd) : ViewType {
    override fun getViewType() = Constant.ViewTypesIds.AD
}
