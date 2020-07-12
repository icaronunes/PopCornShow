package loading.firebase

enum class TypeMediaFireBase {
	TVSHOW {
		override fun type() = "tvshow"
	},
	MOVIE {
		override fun type() = "movie"
	};

	abstract fun type(): String
}