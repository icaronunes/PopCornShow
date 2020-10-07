package error

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import br.com.icaro.filme.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import utils.gone
import utils.invisible
import utils.kotterknife.findView
import utils.visible

class BottomSheetError : BottomSheetDialogFragment() {
	private lateinit var callBack: CallBackError
	private val tryAgain: AppCompatButton by findView(R.id.fragment_error_btn)
	private val msg: TextView by findView(R.id.fragment_error_msg)
	private val img: ImageView by findView(R.id.fragment_error_img)
	private val close: ImageView by findView(R.id.fragment_error_close)
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View? {
		return inflater.inflate(R.layout.fragment_error, container, false)
	}

	override fun onStart() {
		super.onStart()
		isCancelable = false
		val sheetContainer = requireView().parent as? ViewGroup ?: return
		sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		callBack.text().let {
			if (it.isNotEmpty()) {
				msg.apply {
					text = it
					visible()
				}
			} else msg.apply {
				invisible()
				msg.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
				msg.isFocusable = false
			}
		}
		handleClose()
		if (callBack.hasTry()) {
			tryAgain.visible()
			tryAgain.setOnClickListener {
				callBack.tryAgain()
				dismissAllowingStateLoss()
			}
		} else {
			tryAgain.gone()
		}
	}

	private fun handleClose() {
		close.setOnClickListener {
			if (callBack.close()) callBack.closeFunc() else this@BottomSheetError.dismissAllowingStateLoss()
		}
	}

	override fun onResume() {
		super.onResume()
		setupPager()
	}

	private fun setupPager() {
		val bottomSheetBehavior: BottomSheetBehavior<View> = BottomSheetBehavior
			.from(view?.parent as View)
		bottomSheetBehavior.isHideable = false
		bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
	}

	override fun getTheme() = R.style.Theme_MaterialComponents_Light_BottomSheetDialog
	fun newInstance(callBackError: CallBackError): BottomSheetError {
		return BottomSheetError().apply {
			this.callBack = callBackError
		}
	}
}


