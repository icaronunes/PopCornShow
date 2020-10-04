package domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Keep
data class Credits(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("cast")
    val cast: List<CastItem> = listOf(),

    @field:SerializedName("crew")
    val crew: List<CrewItem> = listOf()
) : Serializable
