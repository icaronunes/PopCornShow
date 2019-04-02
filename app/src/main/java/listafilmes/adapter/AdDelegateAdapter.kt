package listafilmes.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import domain.ViewType
import domain.movie.ListAd
import kotlinx.android.synthetic.main.ad_unified.view.*
import pessoaspopulares.adapter.ViewTypeDelegateAdapter


class AdDelegateAdapter : ViewTypeDelegateAdapter {
	
	lateinit var context: Context
	
	override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.ad_unified, parent, false)
		return AdAdapter(view)
	}
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
		val adview = holder.itemView.findViewById<UnifiedNativeAdView>(R.id.ad_view)
		val nativeAd = (item as ListAd).unifiedNativeAd
		(holder as AdAdapter)
				.populateNativeAdView(nativeAd, adview)
	}
	
	
	inner class AdAdapter(val view: View) : RecyclerView.ViewHolder(view) {
		
		fun populateNativeAdView(nativeAd: UnifiedNativeAd,
		                         adView: UnifiedNativeAdView) = with(itemView) {
			
			// Some assets are guaranteed to be in every UnifiedNativeAd.
			ad_headline.text = nativeAd.headline
			adView.headlineView = ad_headline
			
			ad_body.text = nativeAd.body
			adView.bodyView = ad_body
			
			ad_call_to_action.text = nativeAd.callToAction
			adView.callToActionView = ad_call_to_action
			
			// These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
			// check before trying to display them.
			if (nativeAd.icon !== null) ad_icon.setImageDrawable(nativeAd.icon.drawable)
			adView.iconView = ad_icon
			
			if (nativeAd.price !== null) ad_price.text = nativeAd.price
			adView.priceView = ad_price
			
			if (nativeAd.store !== null) ad_store.text = nativeAd.store
			adView.storeView = ad_store
			
			if (nativeAd.starRating !== null) ad_stars.rating = nativeAd.starRating.toFloat()
			adView.starRatingView = ad_stars
			
			if (nativeAd.advertiser !== null) ad_advertiser.text = nativeAd.advertiser
			adView.advertiserView = ad_advertiser
			
			// Assign native ad object to the native view.
			adView.mediaView = ad_media
			adView.setNativeAd(nativeAd)
		}
	}
}
