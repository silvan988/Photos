package br.edu.ifsp.scl.sdm.photos.model

import android.content.Context
import br.edu.ifsp.scl.sdm.photos.ui.MainActivity.Companion.PRODUCTS_ENDPOINT
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_OK

class JsonPlaceHolder(context: Context) {
    companion object {

        @Volatile
        private var INSTANCE: JsonPlaceHolder? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: JsonPlaceHolder(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>){
        requestQueue.add(request)
    }

    class ProductListRequest(
        private val responseListener: Response.Listener<PruductList>,
        errorListener: Response.ErrorListener
    ): Request<PruductList>(Method.GET, PRODUCTS_ENDPOINT, errorListener) {

        override fun parseNetworkResponse(response: NetworkResponse?): Response<PruductList>? =
            if (response?.statusCode == HTTP_OK || response?.statusCode == HTTP_NOT_MODIFIED) {
                String(response.data).run {
                    Response.success(
                        Gson().fromJson(this, PruductList::class.java),
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            } else {
                Response.error(VolleyError())
            }

        override fun deliverResponse(response: PruductList?) {
            responseListener.onResponse(response)
        }
    }
}