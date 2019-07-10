package domain.movie

import com.google.gson.annotations.SerializedName
import domain.ViewType
import utils.Constantes

data class Date(

        @field:SerializedName("maximum")
        val maximum: String? = null,

        @field:SerializedName("minimum")
        val minimum: String? = null
) : ViewType {
    override fun getViewType() = Constantes.BuscaConstants.NEWS
}