package login

import android.app.Application
import applicaton.BaseViewModel
import java.util.regex.Pattern

class LoginViewModel(app: Application) : BaseViewModel(app) {
	fun validParamets(login: String, pass: String, repetPass: String): Boolean {
		if (EMAIL_ADDRESS_PATTERN.matcher(login).matches()) {
			if (pass === repetPass && repetPass.length > 6) {
				return true
			}
		}
		return false
	}
}

val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
	"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
		"\\@" +
		"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
		"(" +
		"\\." +
		"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
		")+"
)
