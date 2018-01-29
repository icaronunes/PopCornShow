package domain.person

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Person(

	@field:SerializedName("birthday")
	val birthday: String? = null,

	@field:SerializedName("also_known_as")
	val alsoKnownAs: List<String?>? = null,

	@field:SerializedName("images")
	val images: Images? = null,

	@field:SerializedName("gender")
	val gender: Int? = null,

	@field:SerializedName("imdb_id")
	val imdbId: String? = null,

	@field:SerializedName("profile_path")
	val profilePath: String? = null,

	@field:SerializedName("biography")
	val biography: String? = null,

	@field:SerializedName("deathday")
	val deathday: String? = null,

	@field:SerializedName("external_ids")
	val externalIds: ExternalIds? = null,

	@field:SerializedName("place_of_birth")
	val placeOfBirth: String? = null,

	@field:SerializedName("tagged_images")
	val taggedImages: TaggedImages? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("combined_credits")
	val combinedCredits: CombinedCredits? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("homepage")
	val homepage: String? = null
): Serializable