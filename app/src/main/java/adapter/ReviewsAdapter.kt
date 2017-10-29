package adapter

import activity.Site
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.icaro.filme.R
import domain.MessageItem
import utils.Constantes
import java.util.*

/**
 * Created by icaro on 17/07/16.
 */
class ReviewsAdapter(private var context: Context?, private val reviews: List<MessageItem>) : RecyclerView.Adapter<ReviewsAdapter.FilmeViewHolder>() {

    private val importante = mutableListOf<String>(
            "New York Times",
            "Hugo Gomes",
            "Los Angeles Times",
            "washington post",
            "french",
            "spanish",
            "india",
            "italian",
            "Hollywood News",
            "rolling stone",
            "Metro us",
            "cbs news",
            "pt-br",
            "portugueses",
            "portuguese",
            "brasil",
            Locale.getDefault().displayName,
            Locale.getDefault().displayCountry,
            Locale.getDefault().displayLanguage
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.FilmeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reviews, parent, false)
        context = view.context
        return FilmeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewsAdapter.FilmeViewHolder, position: Int) {

        val reportagem = reviews[position]

        holder.author.text = if (reportagem.attr != null) reportagem.attr else "..."

        holder.reviews_content.text = reportagem.label

        if (verificarImportancia(reportagem)) {
            holder.reviews_content.setTextColor(context?.resources?.getColor(R.color.gray_reviews)!!)
        }

        holder.cardView.setOnClickListener { view ->
            val intent = Intent(context, Site::class.java)
            intent.putExtra(Constantes.SITE, reportagem.url)
            context?.startActivity(intent)

        }
    }

    private fun verificarImportancia(reportagem: MessageItem): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            importante.add(Locale.getDefault().toLanguageTag())
        }

        importante.forEach { s ->
            if (reportagem.label.toLowerCase().contains(s.toLowerCase())){
                return true
            }
        }
        return false
    }

    override fun getItemCount(): Int {
        return if (reviews.isNotEmpty()) {
            reviews.size
        } else {
            0
        }
    }

    inner class FilmeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val author: TextView = itemView.findViewById<TextView>(R.id.author)
         val reviews_content: TextView = itemView.findViewById<TextView>(R.id.content_reviews)
         val cardView: CardView = itemView.findViewById<CardView>(R.id.card_view_reviews)

    }
}
