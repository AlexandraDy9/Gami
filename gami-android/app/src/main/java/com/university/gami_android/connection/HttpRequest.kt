package com.university.gami_android.connection

import android.os.AsyncTask
import com.university.gami_android.preferences.PreferenceHandler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException


class HttpRequest<T>(private val networkCallback: NetworkCallback<T>) : AsyncTask<Call<T>, Void, HttpResponse<T>>() {

    override fun doInBackground(vararg calls: Call<T>): HttpResponse<T> {
        val httpResponse: HttpResponse<T> =
            HttpResponse()
        val call = calls[0]
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                if (!response.headers().get("Authorization").isNullOrBlank())
                    PreferenceHandler.setAuthorization(response.headers().get("Authorization").toString())
                httpResponse.response = response.body()
                httpResponse.isSuccessful = true
                return httpResponse
            }
            val jsonObject = JSONObject(response.errorBody()?.string()!!)
            httpResponse.error = jsonObject.get("message").toString()
            return httpResponse
        } catch (e: JSONException) {
            httpResponse.error = e.message
            return httpResponse
        } catch (e: IOException) {
            httpResponse.error = e.message
            return httpResponse
        }

    }

    override fun onPostExecute(tHttpResponse: HttpResponse<T>) {
        if (tHttpResponse.isSuccessful) {
            networkCallback.onSuccess(tHttpResponse.response)
        } else {
            networkCallback.onError(tHttpResponse.error)
        }
    }
}