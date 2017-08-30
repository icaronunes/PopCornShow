package domain

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by icaro on 29/08/17.
 */
data class Company(

    @JsonProperty("description")
    var description: String? = null,
    @JsonProperty("headquarters")
    var headquarters: String? = null,
    @JsonProperty("homepage")
    var homepage: String? = null,
    @JsonProperty("logo_path")
    var logoPath: String? = null,
    @JsonProperty("id")
    var id: Int? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("parent_company")
    var parentCompany: Company? = null
)


