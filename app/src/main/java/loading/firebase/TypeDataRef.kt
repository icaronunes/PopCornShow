package loading.firebase

enum class TypeDataRef {

	RATED {
		override fun type() = BaseFireBase.RATED
	},
	WATCH {
		override fun type() = BaseFireBase.WATCH
	},
	FALLOW {
		override fun type() = BaseFireBase.FALLOW
	},
	FAVORITY {
		override fun type() = BaseFireBase.FAVORITY

	};

	abstract fun type(): String
}