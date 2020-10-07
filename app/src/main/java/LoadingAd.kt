import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.UtilsKt

class LoadingAd(val context: Context): ILoadingAd {
	override fun fillAdNative(_live: MutableLiveData<MutableList<UnifiedNativeAd>>) {
		GlobalScope.launch {
			withContext(Dispatchers.IO) {
				UtilsKt.getAnuncio(context, 4) {
					val oldList = _live.value
					oldList?.add(it)
					_live.value = oldList
				}
			}
		}
	}
}
