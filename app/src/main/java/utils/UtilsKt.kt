package utils

import android.content.Context
import android.widget.Toast
import br.com.icaro.filme.R
import domain.Api
import domain.UserTvshow
import domain.tvshow.Tvshow
import kotlinx.coroutines.experimental.*

class UtilsKt {

    fun atualizarSerie(context: Context?, serie: Tvshow): UserTvshow {
        val userTvshow = UtilsApp.setUserTvShow(serie)
        var rotina : Job = Job()
        serie.seasons?.forEachIndexed { index, seasonsItem ->
            try {
                rotina =  GlobalScope.launch(Dispatchers.Main) {
                    val season = async(Dispatchers.IO) { Api(context = context!!).getTvSeasons(serie.id!!, seasonsItem?.seasonNumber!!) }.await()
                    userTvshow.seasons!![index].userEps = UtilsApp.setEp2(season)

                }
            } catch (ex: Exception) {
                Toast.makeText(context, context.getString(R.string.ops), Toast.LENGTH_SHORT).show()
            }
        }
        if (rotina.isActive || rotina.isCompleted) rotina.cancel()
        return userTvshow
    }
}
