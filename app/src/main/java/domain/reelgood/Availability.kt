package domain.reelgood

import com.google.gson.annotations.SerializedName

data class Availability(
    @SerializedName("access_type")
    val accessType: Int, // 3
    @SerializedName("purchase_cost_hd")
    val purchaseCostHd: Double, // 12.99
    @SerializedName("purchase_cost_sd")
    val purchaseCostSd: Double, // 12.99
    @SerializedName("rental_cost_hd")
    val rentalCostHd: Double, // 3.99
    @SerializedName("rental_cost_sd")
    val rentalCostSd: Double, // 2.99
    @SerializedName("source_data")
    val sourceData: SourceData,
    @SerializedName("source_id")
    val sourceId: String, // verizon_on_demand-verizon_on_demand-purchase
    @SerializedName("source_name")
    val sourceName: String // verizon_on_demand
)