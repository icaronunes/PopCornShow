package utils

import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.Http2Reader
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

open class ApiSingleton {

	val gson: Gson = Gson()

	companion object {
		class LoggingInterceptor : Interceptor {
			@Throws(IOException::class)
			override fun intercept(chain: Interceptor.Chain): Response {
				val request = chain.request()
				var retryCount = 0
				val RETRY_MAX_COUNT = 1

				var response = chain.proceed(request)
                while (!response.isSuccessful && retryCount < RETRY_MAX_COUNT) {
                    retryCount++;
                    response.close();
                    response = chain.proceed(request);
                }
				Http2Reader.logger.info("Sending request ${request.url} with Method - ${request.method}")
				when (response.code) {
					in 400..499 -> {
						val jsonObject = JSONObject()
						try {
							jsonObject.put("code", response.code)
							jsonObject.put("status", "Failure")
							jsonObject.put("message", "Failure")
							val contentType: MediaType? = response.body!!.contentType()
							val body: ResponseBody =
								ResponseBody.create(contentType, jsonObject.toString())
							Http2Reader.logger.info(body.string())
						} catch (e: JSONException) {
							e.printStackTrace()
						}
					}
					200 -> {
						val jsonObject = JSONObject()
						try {
							jsonObject.put("code", 200)
							jsonObject.put("status", "OK")
							jsonObject.put("message", "Successfull")
							val contentType: MediaType? = response.body!!.contentType()
							val body: ResponseBody =
								ResponseBody.create(contentType, jsonObject.toString())
							Http2Reader.logger.info(body.string())
						} catch (e: JSONException) {
							e.printStackTrace()
						}
					}
				}
				return response
			}
		}

		private val client: OkHttpClient by lazy {
			OkHttpClient.Builder()
				.addInterceptor(LoggingInterceptor())
				.build()
		}

		fun executeCall(url: String, func: Callback) = client.newCall(
			Request.Builder()
				.url(url)
				.get()
				.build()
		).enqueue(func)

		fun executeCall(url: String, postRequest: RequestBody, func: Callback) = client.newCall(
			Request.Builder()
				.url(url)
				.post(postRequest)
				.build()
		).enqueue(func)
	}
}