package domain.movie

import androidx.annotation.Keep
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ViewType
import utils.Constant

@Keep
data class ListAd(val unifiedNativeAd: UnifiedNativeAd) : ViewType {
    override fun getViewType() = Constant.ViewTypesIds.AD
}
