package adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.UserTvshow
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 02/12/16.
 */
class SeguindoRecycleAdapter(private val context: FragmentActivity?, private val userTvshows: MutableList<UserTvshow>?) :
        RecyclerView.Adapter<SeguindoRecycleAdapter.SeguindoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeguindoRecycleAdapter.SeguindoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.seguindo_tvshow, parent, false)
        return SeguindoViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeguindoRecycleAdapter.SeguindoViewHolder, position: Int) {
        val (nome, id, _, _, poster, _, _, desatualizada) = userTvshows!![position]
        Picasso.get().load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + poster!!)
                .into(holder.poster, object : Callback {
                    override fun onSuccess() {}

                    override fun onError(e: Exception) {
                        holder.title.text = nome
                        holder.poster.setImageResource(R.drawable.poster_empty)
                        holder.title.visibility = View.VISIBLE
                    }
                })

        if (desatualizada) {
            holder.circulo.visibility = View.VISIBLE
        } else {
            holder.circulo.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TvShowActivity::class.java)
            intent.putExtra(Constantes.TVSHOW_ID, id)
            intent.putExtra(Constantes.NOME_TVSHOW, nome)
            intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
            context?.startActivity(intent)
        }

    }

    fun add(tvFire: UserTvshow) {
        userTvshows!!.add(tvFire)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return userTvshows?.size ?: 0
    }

    fun addAtualizado(tvFire: UserTvshow) {
        userTvshows?.forEachIndexed { index, userTvshow ->
            if (userTvshow.id == tvFire.id) {
                userTvshows[index] = tvFire
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    inner class SeguindoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val poster: ImageView = itemView.findViewById<View>(R.id.seguindo_imageView) as ImageView
        val title: TextView = itemView.findViewById<View>(R.id.seguindo_title) as TextView
        val circulo: ImageView = itemView.findViewById(R.id.seguindo_circulo)

    }
}