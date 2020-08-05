package domain.reelgood.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import domain.ViewType
import kotlinx.android.parcel.Parcelize
import utils.Constant

@Suppress("PLUGIN_WARNING")
@Parcelize
class Availability : ViewType, Parcelable {
    override fun getViewType(): Int {
        return when (sourceName) {
            "starz" -> Constant.ReelGood.STARZ
            "netflix" -> Constant.ReelGood.NETFLIX
            "hulu_plus" -> Constant.ReelGood.HULU
            "google_play" -> Constant.ReelGood.GOOGLEPLAY
            "amazon_buy","amazon_prime" -> Constant.ReelGood.AMAZON
            "hbo" -> Constant.ReelGood.HBO
            "adult_swim_tveverywhere" -> Constant.ReelGood.ADULT_SWIM
            "fubo_tv" -> Constant.ReelGood.FUBO
            else -> Constant.ReelGood.WEB
        }
    }

    @SerializedName("access_type")
    val accessType: Int = 0
    @SerializedName("purchase_cost_hd")
    val purchaseCostHd: Double? = null // 12.99
    @SerializedName("purchase_cost_sd")
    val purchaseCostSd: Double? = null // 12.99
    @SerializedName("rental_cost_hd")
    val rentalCostHd: Double? = null // 3.99
    @SerializedName("rental_cost_sd")
    val rentalCostSd: Double? = null // 2.99
    @SerializedName("source_data")
    val sourceData: SourceData? = null
    @SerializedName("source_id")
    val sourceId: String? = null // verizon_on_demand-verizon_on_demand-purchase
    @SerializedName("source_name")
    var sourceName: String? = null // verizon_on_demand
}