package error

import Txt
import android.app.Activity

class ErrorTryDefault(val activity: Activity,val func: () -> Unit) : CallBackError {
	override fun tryAgain() { func() }
	override fun close() = true
	override fun text() = activity.getString(Txt.ops)
	override fun hasTry() = true
	override fun closeFunc() {
		activity.finish()
	}
}