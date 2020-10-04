package utils

import com.google.gson.Gson
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
open class CallBackApi<T>(
	private val cont: CancellableContinuation<T>,
	private val data: Class<T>,
) : Callback {
	override fun onFailure(call: Call, e: IOException) {
		cont.resumeWithException(e)
	}

	override fun onResponse(call: Call, response: Response) {
		try {
			if (response.isSuccessful) {
				val json = response.body?.string()
				val data = Gson().fromJsonWithLog(json, data)
				cont.resume(data)
			} else {
				cont.resumeWithException(Exception(response.message))
			}
		} catch (e: Exception) {
			cont.resumeWithException(e)
		}
	}
}