package domain.reelgood

import com.google.gson.annotations.SerializedName
import domain.ViewType
import utils.Constantes

class Availability : ViewType {
    override fun getViewType(): Int {
        return when (sourceName) {
            "starz" -> Constantes.ReelGood.STARZ
            "netflix" -> Constantes.ReelGood.NETFLIX
            "hulu_plus" -> Constantes.ReelGood.HULU
            "google_play" -> Constantes.ReelGood.GOOGLEPLAY
            "amazon_buy" -> Constantes.ReelGood.AMAZON
            "hbo" -> Constantes.ReelGood.HBO
            else -> Constantes.ReelGood.WEB
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
    val sourceName: String? = null // verizon_on_demand
}