package customview

import ID
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.icaro.filme.R
import com.facebook.shimmer.ShimmerFrameLayout
import customview.LoadingShimmer.PaymentLoadingsType.*
import utils.kotterknife.findView
import kotlin.random.Random

/**
 *  Shimmer PopCorn
 */
class LoadingShimmer : ConstraintLayout {
	//TODO - Criar metodo que receber um layout pronto

	companion object {
		private const val TIMEREFREASH = 3000L
		private const val TIMECONST = 400L
		private const val MARGINTITLEBOTTOM = 20
	}

	private val containe: LinearLayout by findView(ID.contaier_shimmer)
	private val shimmer: ShimmerFrameLayout by findView(ID.fallow_shimmer)
	val next by NextLoadingDelegate(context)
	val title by TextLoadingDelegate(context)
	val photo by PhotoLoadingDelegate(context)
	val description by DescriptionLoadingDelegate(context)

	sealed class PaymentLoadingsType {
		class Title(val topM: Int = 10, val rightM: Int = 0, val bottomM: Int = 0) :
			PaymentLoadingsType()

		object Text : PaymentLoadingsType()
		object CalendarTv : PaymentLoadingsType()
		object Photo : PaymentLoadingsType()
		class ListDescriptionLabel(val quant: Int = 5) : PaymentLoadingsType()
		object Description : PaymentLoadingsType()
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

	fun createCustomLoading(vararg list: PaymentLoadingsType) {
		containe.removeAllViewsInLayout()
		addViews(*list)
	}

	fun defaultLoading() {
		addViews(
			Text,
			Title(bottomM = MARGINTITLEBOTTOM),
			ListDescriptionLabel()
		)
	}

	/**
	createRandom não tem animacao consistente no aparecimento das views
	 */
	fun createRandom() {
		containe.removeAllViewsInLayout()
		for (i in 1..5) {
			(when (Random.nextInt((3 - 1) + 1) + 1) {
				1 -> addViews(Photo)
				2 -> addViews(Title())
				3 -> addViews(ListDescriptionLabel())
			})
		}
		containe.postOnAnimationDelayed({
			createRandom() // ???
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
				is Title -> createViewTitle(
					rightM = paymentLoadingsType.rightM,
					topM = paymentLoadingsType.topM,
					bottomM = paymentLoadingsType.bottomM
				)
				is Text -> title
				is ListDescriptionLabel -> createListDescription(paymentLoadingsType.quant, index)
				is CalendarTv -> next
				is Photo -> photo
				is Description -> description
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
}
