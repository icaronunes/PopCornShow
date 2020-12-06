package pessoaspopulares.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.PersonItem
import domain.ViewType
import kotlinx.android.synthetic.main.adapter_person_popular.view.img_popular_person
import kotlinx.android.synthetic.main.adapter_person_popular.view.progress
import kotlinx.android.synthetic.main.adapter_person_popular.view.text_person_name
import pessoa.activity.PersonActivity
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCacheAndHolder
import utils.visible

class PersonDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        PersonViewHoder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType?, context: Context?) {
        holder as PersonViewHoder
        holder.bind(item as PersonItem)
    }

    inner class PersonViewHoder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_person_popular, parent, false)) {

        fun bind(item: PersonItem) = with(itemView) {
            progress.visible()
            Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + item.profilePath).into(img_popular_person)
            img_popular_person.setPicassoWithCacheAndHolder(item.profilePath, 3,
                { progress.gone() },
                { progress.gone() },
                img_erro = R.drawable.person,
                holder = R.drawable.person)
            text_person_name.text = item.name

            setOnClickListener {
                val intent = Intent(context, PersonActivity::class.java)
                intent.putExtra(Constant.NOME_PERSON, item.name)
                intent.putExtra(Constant.ID, item.id)
                context.startActivity(intent)
            }
        }
    }
}
