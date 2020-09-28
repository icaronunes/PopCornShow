package domain

import com.google.gson.annotations.SerializedName

/**
 * Created by icaro on 29/08/17.
 */
data class Company(

    @SerializedName("description")
    var description: String? = null,
    @SerializedName("headquarters")
    var headquarters: String? = null,
    @SerializedName("homepage")
    var homepage: String? = null,
    @SerializedName("logo_path")
    var logo_path: String? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("parent_company")
    var parentCompany: Company? = null
)
