package utils

/**
 * Created by icaro on 02/07/16.
 */
object Constant {
	const val ZERO: Float = 0.0f
	const val BASEMOVIEDB_TV = "https://www.themoviedb.org/tv/"
	const val BASEMOVIEDB_MOVIE = "https://www.themoviedb.org/movie/"
	const val IMDB = "https://www.imdb.com/title/"
	const val ROTTENTOMATOESMOVIE = "https://www.rottentomatoes.com/m/"
	const val ROTTENTOMATOESTV = "https://www.rottentomatoes.com/tv/"
	const val METACRITICMOVIE = "http://www.metacritic.com/movie/"
	const val METACRITICTV = "http://www.metacritic.com/tv/"
	const val NAV_DRAW_ESCOLIDO = "check"
	const val ABA = "aba"
	const val ID = "id"
	const val NOME_FILME = "nome_filme"
	const val ENDERECO = "endereco"
	const val YOU_TUBE_KEY = "youtube_key"
	const val SINOPSE = "sinopse"
	const val ID_REEL = "reel_id"
	const val COLOR_TOP = "color_top"
	const val COLOR = "color"
	const val LISTA_ID = "lista_id"
	const val LISTA_NOME = "lista_nome"
	const val NOME_PERSON = "nome_person"
	const val PERSON = "person"
	const val SITE = "site"
	const val POSICAO = "posicao"
	const val NOME_TVSHOW = "tvshow_nome"
	const val TV = "tv"
	const val MOVIE = "movie"
	const val SIMILARES = "similares_filme"
	const val TVSEASONS = "tvseason"
	const val TEMPORADA_ID = "temporada_id"
	const val ARTWORKS = "artworks"
	const val BUNDLE = "bundle"
	const val NAME = "name"
	const val EPSODIO = "epsodio"
	const val MEDIATYPE = "mediatype"
	const val MAIN = "main"
	const val SEGUINDO = "seguindo"
	const val TEMPORADA_POSITION = "temporada_position"
	const val TWITTER_URL = "@appopcorn"
	const val PRODUCAO = "producao"
	const val POSTER = "poster"
	const val WORK = "work"

	object StatusRunninTv {
		const val RETURN = "Returning Series"
		const val ENDED = "Ended"
		const val CANCELED = "Canceled"
		const val PRODUCTION = "In Production"
	}

	object ViewTypesIds {
		const val NEWS = 1
		const val LOADING = 2
		const val AD = 3
		const val TVSHOW = 4
		const val FALLOW = 5
		const val CREWS = 6
		const val CAST = 7
	}

	object ReelGood {
		const val LOADING = 1
		const val GOOGLEPLAY = 2
		const val NETFLIX = 3
		const val HBO = 4
		const val STARZ = 5
		const val HULU = 6
		const val AMAZON = 7
		const val WEB = 8
		const val ADULT_SWIM = 9
		const val FUBO = 10
	}

	object TypeStream {
		const val huluPackage = "com.hulu.plus"
		const val netflixPackage = "com.netflix.mediaclient"
		const val googleVideosPackage = "com.google.android.videos"
		const val hboPackage = "com.hbo.broadband.br"
		const val starzPackage = "com.starz.starzplay.android"
		const val amazonPackage = "com.amazon.avod.thirdpartyclient"
		const val adultswin = "com.adultswim.videoapp.android"
	}

	object Signal {
		const val MOVIE = "MovieDetailsActivity"
		const val TVSHOW = "TvshowActivity"
		const val MOVIESLIST = "FilmesActivity"
		const val TVSHOWLIST = "TvShowsActivity"
		const val LISTGENERIC = "ListGenericActivity"
		const val TRAILER = "TrailerActivity"
		const val VIDEO = "TreilerActivity"
		const val SITE = "SiteActivity"
		const val PRODUTORA = "ProdutoraActivity"
		const val SIMILARES = "SimilaresActivity"
		const val FOTOPERSON = "FotoPersonActivity"
		const val TEMPORADA = "TemporadaActivity"
	}

	object ListOnTheMovie {
		const val OSCAR = "28"
	}
}
