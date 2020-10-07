
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.formats.UnifiedNativeAd

interface ILoadingAd {
	fun fillAdNative(_live: MutableLiveData<MutableList<UnifiedNativeAd>>)
}
