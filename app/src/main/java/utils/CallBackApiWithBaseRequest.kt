package utils

import applicaton.BaseViewModel.*
import applicaton.BaseViewModel.BaseRequest.*
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
open class CallBackApiWithBaseRequest<T>(
	private val cont: CancellableContinuation<BaseRequest<T>>,
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
				cont.resume(Success(data))
			} else {
				cont.resume(Failure(java.lang.Exception(response.message)))
			}
		} catch (e: Exception) {
			cont.resumeWithException(e)
		}
	}
}