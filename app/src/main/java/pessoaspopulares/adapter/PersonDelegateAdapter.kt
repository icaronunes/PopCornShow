package pessoaspopulares.adapter

import pessoa.activity.PersonActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.PersonItem
import domain.ViewType
import kotlinx.android.synthetic.main.adapter_person_popular.view.*
import kotlinx.android.synthetic.main.include_progress.view.*
import utils.Constantes
import utils.UtilsApp

class PersonDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            PersonViewHoder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        holder as PersonViewHoder
        holder.bind(item as PersonItem)

    }

    inner class PersonViewHoder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_person_popular, parent, false)) {

        fun bind(item: PersonItem) = with(itemView) {

            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + item.profilePath).into(img_popular_person)
            text_person_name.text = item.name
            progress.visibility = View.GONE
            itemView.setOnClickListener({
                val intent = Intent(context, PersonActivity::class.java )
                intent.putExtra(Constantes.NOME_PERSON, item.name)
                intent.putExtra(Constantes.PERSON_ID, item.id)
                context.startActivity(intent)
            })
        }
    }
}
