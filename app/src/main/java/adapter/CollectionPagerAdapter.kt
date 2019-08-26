package adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.colecao.PartsItem
import filme.activity.FilmeActivity
import utils.Constantes
import utils.UtilsApp
import java.lang.Exception

/**
 * Created by icaro on 22/07/16.
 */
class CollectionPagerAdapter(private val info: List<PartsItem?>?, private val context: Context) : PagerAdapter() {
    private lateinit var imageView: ImageView
    private lateinit var linearLayout: LinearLayout
    private lateinit var nome: TextView

    override fun getCount(): Int {
        return if (info?.isNotEmpty()!!) info.size else 0
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = info?.get(position)
        val view = LayoutInflater.from(context).inflate(R.layout.collection, container, false)
        nome = view.findViewById(R.id.dateCollection)
        linearLayout = view.findViewById(R.id.collection_linear)
        imageView = view.findViewById(R.id.img_collection)
        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 5)) + item?.posterPath)
                .error(R.drawable.poster_empty)
                .into(imageView, object : Callback {
                    override fun onError(e: Exception?) {

                    }

                    override fun onSuccess() {
                        loadPaletteCollection()
                    }

                })



        imageView.setOnClickListener({
            val intent = Intent(context, FilmeActivity::class.java)
            intent.putExtra(Constantes.FILME_ID, item?.id)
            intent.putExtra(Constantes.NOME_FILME, item?.title)
            context.startActivity(intent)
        })




        var ano = "xxxx"

        if (!item?.releaseDate.isNullOrBlank()){

            if (item?.releaseDate?.length!! >= 4){ ano = item.releaseDate.substring(0,4)  }
        }

        if (!item?.title.isNullOrBlank()) {
            val tituloData = item?.title +
                    " - " + ano
            nome.text = tituloData
        }
        container.addView(view)
        return view

    }

    private fun loadPaletteCollection() {
        if (imageView.drawable != null) {
            val drawable = imageView.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val builder = Palette.Builder(bitmap)
            val swatch = builder.generate().vibrantSwatch
            if (swatch != null) {
                linearLayout.setBackgroundColor(swatch.rgb)
                nome.setTextColor(swatch.titleTextColor)
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

}