package error

import Txt
import android.app.Activity

class ErrorDefault(val activity: Activity) : CallBackError {
	override fun close() = true
	override fun text() = activity.getString(Txt.ops)
	override fun hasTry() = false
	override fun closeFunc() {
		activity.finish()
	}
}