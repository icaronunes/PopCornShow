package domain

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Keep
data class Videos(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("results")
    val results: MutableList<ResultsVideosItem?>? = null
) : Serializable
