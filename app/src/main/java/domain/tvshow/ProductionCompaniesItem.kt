package domain.tvshow

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Keep
data class ProductionCompaniesItem(

    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("logo_path")
    val logo: String? = null,
    @field:SerializedName("origin_country")
    val originCountry: String? = null
) : Serializable