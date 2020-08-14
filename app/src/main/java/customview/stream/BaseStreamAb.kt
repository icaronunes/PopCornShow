package customview.stream

import android.graphics.Bitmap
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import domain.reelgood.movie.Availability

abstract class BaseStreamAb {

    abstract val typeStream: String

    fun getStreamWebLink(availability: Availability?, title: String): String {
        if (availability == null) return "https://www.google.com/search?q=$title ${availability?.sourceName ?: ""}"
        return when (availability.sourceName) {
            "imdb_tv" -> "https://www.imdb.com/tv/"
            "showtime" -> "https://www.sho.com/"
            "epix" -> "https://www.epix.com/"
            "microsoft" -> "https://www.microsoft.com/store/movies-and-tv"
            "vudu" -> "https://www.vudu.com/"
            "sony" -> "https://store.playstation.com/en-us/grid/STORE-MSF77008-NRMOVIES_US/1"
            "youtube_purchase" -> "https://www.youtube.com/"
            "verizon_on_demand" -> "https://www.verizon.com/home/mlp/fios-on-demand/"
            "mgo" -> "https://www.fandangonow.com/"
            "itunes" -> "https://itunes.apple.com/us/genre/movies/id33"
            "britbox" -> "https://www.britbox.com/"
            "cbs_all_access" -> "https://www.cbs.com/all-access/"
            "nbc_tvesverywhere", "nbc" -> "https://www.nbc.com/$title"
            "fox_tveverywhere" -> "https://www.fox.com/"
            "tbs" -> "https://www.international.tbs.com/"
            else -> "https://www.google.com/search?q=$title ${availability.sourceName}"
        }
    }

    fun getImgStreamService(availability: Availability?, onResource: (iconSource: Int) -> Unit, onWeb: (bitmap: Bitmap?) -> Unit) {

        if (availability == null) onResource(R.drawable.question)
        getImg(availability?.sourceName, onResource = onResource)
    }

    fun getImg(sourceName: String?, onResource: (iconSource: Int) -> Unit){
        when (sourceName) {
            "imdb_tv" -> onResource(R.drawable.imdb_tv)
            "showtime" -> onResource(R.drawable.showtime)
            "epix" -> onResource(R.drawable.epix)
            "microsoft" -> onResource(R.drawable.microsoft)
            "vudu" -> onResource(R.drawable.vudu)
            "sony" -> onResource(R.drawable.sony)
            "youtube_purchase" -> onResource(R.drawable.youtube)
            "verizon_on_demand" -> onResource(R.drawable.verizon)
            "mgo" -> onResource(R.drawable.mgo)
            "itunes" -> onResource(R.drawable.itunes)
            "britbox" -> onResource(R.drawable.britbox)
            "cbs_all_access" -> onResource(R.drawable.cbs_all_access)
            "nbc", "nbc_tveverywhere" -> onResource(R.drawable.nbc)
            "" -> onResource(R.drawable.nbc)
            "fox_tveverywhere" -> onResource(R.drawable.fox)
            "tbs" -> onResource(R.drawable.tbs)
            "abc", "abc_tveverywhere", "abc_family" -> onResource(R.drawable.abc)
            "acorntv" -> onResource(R.drawable.acorntv)
            "ae_tveverywhere" -> onResource(R.drawable.ae_tveverywhere)
            "amc" -> onResource(R.drawable.amc)
            "amc_premiere" -> onResource(R.drawable.amc_premiere)
            "apple_tv_plus" -> onResource(R.drawable.apple_tv_plus)
            "bbc_america", "bbc_america_tve" -> onResource(R.drawable.bbc_america)
            "bet" -> onResource(R.drawable.bet)
            "bet_plus" -> onResource(R.drawable.bet_plus)
            "bravo" -> onResource(R.drawable.bravo)
            "cartoon_network" -> onResource(R.drawable.cartoon_network)
            "cinemax" -> onResource(R.drawable.cinemax)
            "cnbc" -> onResource(R.drawable.cnbc)
            "comedy" -> onResource(R.drawable.comedy)
            "criterion_channel" -> onResource(R.drawable.criterion_channel)
            "crunchyroll" -> onResource(R.drawable.crunchyroll)
            "dc_universe" -> onResource(R.drawable.dc_universe)
            "disney_channel" -> onResource(R.drawable.disney_channel)
            "disney_plus" -> onResource(R.drawable.disney_plus)
            "diy_network" -> onResource(R.drawable.diy_network)
            "e" -> onResource(R.drawable.e)
            "fandor" -> onResource(R.drawable.fandor)
            "food_network" -> onResource(R.drawable.food_network)
            "funimation" -> onResource(R.drawable.funimation)
            "fx","fx_tveverywhere" -> onResource(R.drawable.fx)
            "fyi" -> onResource(R.drawable.fyi)
            "hallmark_everywhere" -> onResource(R.drawable.hallmark_everywhere)
            "hallmark_movies_now" -> onResource(R.drawable.hallmark_movies_now)
            "hgtv" -> onResource(R.drawable.hgtv)
            "history" -> onResource(R.drawable.history)
            "ifc" -> onResource(R.drawable.ifc)
            "indieflix" -> onResource(R.drawable.indieflix)
            "kanopy" -> onResource(R.drawable.kanopy)
            "lifetime" -> onResource(R.drawable.lifetime)
            "mtv" -> onResource(R.drawable.mtv)
            "mubi" -> onResource(R.drawable.mubi)
            "natgeo" -> onResource(R.drawable.natgeo)
            "nick" -> onResource(R.drawable.nick)
            "science_go" -> onResource(R.drawable.science_go)
            "shudder" -> onResource(R.drawable.shudder)
            "sundance" -> onResource(R.drawable.sundance)
            "syfy" -> onResource(R.drawable.syfy)
            "tcm" -> onResource(R.drawable.tcm)
            "tlc_go" -> onResource(R.drawable.tlc_go)
            "tnt" -> onResource(R.drawable.tnt)
            "travel" -> onResource(R.drawable.travel)
            "trutv" -> onResource(R.drawable.trutv)
            "tvland" -> onResource(R.drawable.tvland)
            "usa" -> onResource(R.drawable.usa)
            "vh1" -> onResource(R.drawable.vh1)
            "viceland" -> onResource(R.drawable.viceland)

            "youtube_premium" -> onResource(R.drawable.youtube_premium)
            "starz" -> onResource(R.drawable.starz)
            "netflix" -> onResource(R.drawable.netflix_stream)
            "hulu_plus" -> onResource(R.drawable.hulu)
            "google_play" -> onResource(R.drawable.google)
            "amazon_buy","amazon_prime" -> onResource(R.drawable.amazon)
            "hbo" -> onResource(R.drawable.hbo)
            "adult_swim_tveverywhere" -> onResource(R.drawable.adult_swim)
            "fubo_tv" -> onResource(R.drawable.fubo_tv)
            else -> {
                onResource(R.drawable.question)
                Crashlytics.log("Stream Question - $sourceName")
            }
        }
    }

    fun getSomeReference(availability: Availability?, type: TypeEnumStream): String? {
        val references = availability?.sourceData?.references?.android
            ?: availability?.sourceData?.references?.web
            ?: availability?.sourceData?.references?.ios

        return when(type) {
            TypeEnumStream.EP -> references?.epId
            TypeEnumStream.MOVIE -> references?.movieId
            TypeEnumStream.TV -> references?.showId
        }
    }

    fun getSomeLink(availability: Availability?, default: String?): String? =
        availability?.sourceData?.links?.android
        ?: availability?.sourceData?.links?.web
        ?: availability?.sourceData?.links?.ios ?: default

}
