package busca.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import br.com.icaro.filme.R.drawable
import domain.ViewType
import domain.search.KnownFor
import domain.search.Result
import filme.activity.MovieDetailsActivity
import pessoa.activity.PersonActivity
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.enums.EnumTypeMedia
import utils.setPicasso
import utils.setPicassoWithCache

class SearchPersonAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = HolderSearch(parent)
    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as HolderSearch).bind(person = item as Result)
    }

    inner class HolderSearch(val parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.search_list_person_adapter, parent, false)) {

        private val poster: ImageView = itemView.findViewById(R.id.img_search)
        private val searchNome: TextView = itemView.findViewById(R.id.search_name)
        private val linear: LinearLayoutCompat = itemView.findViewById(R.id.container_know)

        fun bind(person: Result) = with(itemView) {
            poster.setPicassoWithCache(person.profilePath, 4, img_erro = drawable.person)
            person.name.let {
                searchNome.text = it
                contentDescription = it
            }
            contentDescription = "$contentDescription - ${context.getString(R.string.know_for)}"
            itemView.setOnClickListener {
                context.startActivity(Intent(context, PersonActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constantes.PERSON_ID, person.id)
                    putExtra(Constantes.NOME_PERSON, person.name)
                })
            }

            setKnow(person)
            this
        }

        private fun setKnow(person: Result) {
            linear.removeAllViews() // Por que precisa disso?
            if (!person.knownFor.isNullOrEmpty())
            person.knownFor.filter { !it.posterPath!!.isBlank() }
                .take(3).forEach { media ->
                    fun getNameByType() = when (media.mediaType) {
                        EnumTypeMedia.MOVIE.type -> {
                            "${media.title},"
                        }
                        EnumTypeMedia.TV.type -> {
                            "${media.name},"
                        }
                        else -> ""
                    }
                    itemView.contentDescription = "${itemView.contentDescription} ${getNameByType()}"
                    Log.d("person", media.name)
                    val poster = LayoutInflater.from(parent.context).inflate(R.layout.poster_person, null, false)
                    val img = poster.findViewById<ImageView>(R.id.img_poster_grid).setPicasso(media.posterPath, 2).apply {
                        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                        isFocusableInTouchMode = false
                    }
                    poster.setOnClickListener { callIntent(parent.context, media) }
                    linear.addView(poster)
                }
        }

        private fun callIntent(context: Context, knownFor: KnownFor) {
            when (knownFor.mediaType) {
                EnumTypeMedia.MOVIE.type -> {
                    context.startActivity(Intent(itemView.context, MovieDetailsActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                        putExtra(Constantes.FILME_ID, knownFor.id)
                        putExtra(Constantes.NOME_FILME, knownFor.title)
                    })
                }
                EnumTypeMedia.TV.type -> {
                    context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                        putExtra(Constantes.TVSHOW_ID, knownFor.id)
                        putExtra(Constantes.NOME_TVSHOW, knownFor.name)
                    })
                }
            }
        }
    }
}
