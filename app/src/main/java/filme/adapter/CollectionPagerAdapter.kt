package filme.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import domain.colecao.PartsItem
import filme.activity.MovieDetailsActivity
import utils.Constantes
import utils.setPicasso

/**
 * Created by icaro on 22/07/16.
 */
class CollectionPagerAdapter(private val info: List<PartsItem?>?, private val context: Context) : PagerAdapter() {
    private lateinit var imageView: ImageView
    private lateinit var constraintLayout: ConstraintLayout
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
        constraintLayout = view.findViewById(R.id.collection_linear)
        imageView = view.findViewById<ImageView>(R.id.img_collection).apply {

            setOnClickListener {
                context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constantes.FILME_ID, item?.id)
                    putExtra(Constantes.NOME_FILME, item?.title)
                })
            }

            setPicasso(item?.posterPath, 5, { loadPaletteCollection(this.drawable as BitmapDrawable) })
        }

        var ano = "xxxx"

        if (!item?.releaseDate.isNullOrBlank()) {

            if (item?.releaseDate?.length!! >= 4) {
                ano = item.releaseDate.substring(0, 4)
            }
        }

        if (!item?.title.isNullOrBlank()) {
            val tituloData = item?.title +
                    " - " + ano
            nome.text = tituloData
        }
        container.addView(view)
        return view

    }

    private fun loadPaletteCollection(drawable: BitmapDrawable) {
        val bitmap = drawable.bitmap
        val builder = Palette.Builder(bitmap)
        val swatch = builder.generate().vibrantSwatch
        if (swatch != null) {
            constraintLayout.setBackgroundColor(swatch.rgb)
            nome.setTextColor(swatch.bodyTextColor)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

}