package customview

import ID
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.icaro.filme.R
import com.facebook.shimmer.ShimmerFrameLayout
import customview.LoadingShimmer.PaymentLoadingsType.CalendarTv
import customview.LoadingShimmer.PaymentLoadingsType.ListDescriptionLabel
import customview.LoadingShimmer.PaymentLoadingsType.Resumo
import customview.LoadingShimmer.PaymentLoadingsType.Text
import customview.LoadingShimmer.PaymentLoadingsType.Title
import utils.kotterknife.findView
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KProperty


/**
 * TODO: document your custom view class.
 */
class LoadingShimmer : ConstraintLayout {
	//TODO - Criar metodo que receber um layout pronto

	private val TIMEREFREASH = 3000L
	private val TIMECONST = 200L
	private val MARGINTITLERIGHT = 160
	private val MARGINTITLEBOTTOM = 20

	private val containe: LinearLayout by findView(ID.contaier_shimmer)
	private val shimmer: ShimmerFrameLayout by findView(ID.fallow_shimmer)
	val resumo by ResumoLoadingDelegate(context)
	val calendar by PosterLoadingDelegate()
	val title by TextLoadingDelegate(context)

	sealed class PaymentLoadingsType {
		class Title(val topM: Int = 10, val rightM: Int = 0, val bottomM: Int = 0) :
			PaymentLoadingsType()

		object Resumo : PaymentLoadingsType()
		object Text : PaymentLoadingsType()
		object CalendarTv : PaymentLoadingsType()
		class ListDescriptionLabel(val quant: Int = 5) : PaymentLoadingsType()
	}

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
		context,
		attrs,
		defStyle
	)

	init {
		inflate(context, R.layout.loading_fallow_shimmer, this)
//		defaultLoading()
	}

	private fun createViewResumo() = resumo

	fun defaultLoading() {
		addViews(
			Resumo,
			Text,
			Title(bottomM = MARGINTITLEBOTTOM),
			ListDescriptionLabel()
		)
	}

	fun createCustomLoading(vararg list: PaymentLoadingsType) {
		containe.removeAllViewsInLayout()
		addViews(*list)
	}

	/**
	createRandom não tem animacao consistente no aparecimento das views
	 */
	fun createRandom() {
		containe.removeAllViewsInLayout()
		for (i in 1..5) {
			(when (Random.nextInt((3 - 1) + 1) + 1) {
				1 -> addViews(Resumo)
				2 -> addViews(Title())
				3 -> addViews(ListDescriptionLabel())
			})
		}

		containe.postOnAnimationDelayed({
			createRandom()
		}, TIMEREFREASH)
	}

	private fun addAnimationToView(view: View, index: Int) {
		Handler(Looper.getMainLooper()).postDelayed({
			containe.addView(view)
			val animation = loadAnimation(context, R.anim.fab_slide_in_from_left)
			view.startAnimation(animation)
		}, TIMECONST * index)
	}

	private fun addViews(vararg views: PaymentLoadingsType) {
		views.forEachIndexed { index, paymentLoadingsType ->
			val view = when (paymentLoadingsType) {
				is Resumo -> resumo
				is Title -> createViewTitle(
					rightM = paymentLoadingsType.rightM,
					topM = paymentLoadingsType.topM,
					bottomM = paymentLoadingsType.bottomM
				)
				is Text -> title
				is ListDescriptionLabel -> createListDescription(
					paymentLoadingsType.quant,
					index
				)
				is CalendarTv -> calendar
			}
			if (view != null) addAnimationToView(view, index)
		}
	}

	private fun createListDescription(quant: Int = 3, index: Int): View? {
		for (i in 1 until quant) {
			val list by DescriptionLoadingDelegate(context)
			if (list != null) addAnimationToView(list!!, index)
		}
		return null
	}

	private inner class DescriptionLoadingDelegate(val context: Context?) :
		ReadWriteProperty<View?, View?> {

		private val MARGINRIGHTDISCRIPTION = 200
		private val MARGINBOTTOMISCRIPTION = 60

		private var view: View? = null
		override fun getValue(thisRef: View?, property: KProperty<*>): View? {
			return LinearLayout(context).apply {
				orientation = LinearLayout.VERTICAL
				layoutParams =
					LayoutParams(
						LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT
					)
				val label = createViewTitle(rightM = MARGINRIGHTDISCRIPTION)

				val description = createViewTitle().apply {
					layoutParams =
						LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT
						).apply {
							setMargins(0, 0, 0, MARGINBOTTOMISCRIPTION)
						}
				}
				addView(label)
				addView(description)
			}
		}

		override fun setValue(thisRef: View?, property: KProperty<*>, value: View?) {
			this.view = value
		}
	}

	private fun createViewTitle(
		leftPadding: Int = 10,
		topPadding: Int = 10,
		rightPadding: Int = 10,
		bottomPadding: Int = 10,
		topM: Int = 10,
		rightM: Int = 45,
		bottomM: Int = 10
	): View? {
		val title by TextLoadingDelegate(
			context, rightM = rightM, leftPadding = leftPadding,
			topPadding = topPadding,
			rightPadding = rightPadding,
			bottomPadding = bottomPadding,
			topM = topM,
			bottomM = bottomM
		)
		return title
	}

	private inner class ResumoLoadingDelegate(
		private val context: Context,
		private val marginsT: Int = 80,
		private val marginsB: Int = 60
	) : ReadWriteProperty<View?, View> {
		private var value: View? = null

		override fun getValue(thisRef: View?, property: KProperty<*>): View {
			return inflate(context, R.layout.calendar_adapter, null).apply {// Todo
				layoutParams =
					LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
						setMargins(0, 0, 0, marginsB)
						setBackgroundResource(R.drawable.background_loading_shimmer)
					}
			}
		}

		override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
			this.value = value
		}
	}

	private inner class PosterLoadingDelegate : ReadWriteProperty<View?, View> {
		private var value: View? = null

		override fun getValue(thisRef: View?, property: KProperty<*>): View {
			return inflate(context, R.layout.calendar_adapter_loading, null).apply {
			}
		}

		override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
			this.value = value
		}
	}

	//Remover para fora da arquivo
	private inner class TextLoadingDelegate(
		private val context: Context,
		private val leftPadding: Int = 10,
		private val topPadding: Int = 10,
		private val rightPadding: Int = 10,
		private val bottomPadding: Int = 10,
		private val topM: Int = 10,
		private val rightM: Int = 10,
		private val bottomM: Int = 10
	) : ReadWriteProperty<View?, View> {
		private var value: View = TextView(context)

		override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
			this.value = value
		}

		override fun getValue(thisRef: View?, property: KProperty<*>): View {
			return TextView(context).apply {
				setBackgroundResource(R.drawable.background_loading_shimmer)
				setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
				layoutParams = LinearLayout
					.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
						setMargins(0, topM, rightM, bottomM)
					}
			}
		}
	}
}
