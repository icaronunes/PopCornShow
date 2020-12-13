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
import utils.Constant
import utils.isNotNullOrBlank
import utils.onClick
import utils.setPicasso

/**
 * Created by icaro on 22/07/16.
 */
class CollectionPagerAdapter(private val info: List<PartsItem?>?, val context: Context) : PagerAdapter() {

    override fun getCount() = info?.size ?: 0

    override fun isViewFromObject(view: View, obj: Any) = view === obj

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = info?.get(position)
        return BindCollection(container, context).bind(item)
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        (container as ViewPager).removeView(view as View)
    }

    class BindCollection(val container: ViewGroup,val context: Context) {
        private val XXXX = "xxxx"

        private lateinit var constraintLayout: ConstraintLayout
        private lateinit var name: TextView
        private lateinit var reviews: TextView

        fun bind(item: PartsItem?): View {
            return with(LayoutInflater.from(context).inflate(R.layout.collection, container, false)) {
                constraintLayout = findViewById(R.id.collection_linear)
                name = findViewById<TextView>(R.id.nameCollection)
                findViewById<TextView>(R.id.reviews).apply {
                    text = item?.overview ?: ""
                }
                findViewById<ImageView>(R.id.img_collection).apply {
                    onClick {
                        context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                            putExtra(Constant.ID, item?.id)
                            putExtra(Constant.NOME_FILME, item?.title)
                        })
                    }

                    setPicasso(item?.posterPath, 5, sucesso = { loadPaletteCollection(this.drawable as BitmapDrawable) })
                }

                var ano = XXXX
                if (item?.releaseDate.isNotNullOrBlank()) {
                    if (item?.releaseDate?.length!! >= 4) {
                        ano = item.releaseDate.substring(0, 4)
                    }
                }

                if (item?.title.isNotNullOrBlank()) {
                    val tituloData = "${item?.title} - $ano"
                    name.text = tituloData
                }
                container.addView(this@with)
                this
            }
        }

        private fun loadPaletteCollection(drawable: BitmapDrawable) {
            val bitmap = drawable.bitmap
            val builder = Palette.Builder(bitmap)
            val swatch = builder.generate().vibrantSwatch
            if (swatch != null) {
                constraintLayout.setBackgroundColor(swatch.rgb)
                name.setTextColor(swatch.bodyTextColor)
            }
        }
    }
}
