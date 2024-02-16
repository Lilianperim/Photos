package com.example.photos.model

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection

class PlaceHolderJsonApi(context: Context) {

    companion object {
        const val PHOTOS_ENDPOINT = "https://jsonplaceholder.typicode.com/photos/"

        @Volatile
        private var INSTANCE: PlaceHolderJsonApi? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PlaceHolderJsonApi(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }

    class PhotosListRequest(
        private val responseListener: Response.Listener<List<PhotosListItem>>,
        errorListener: Response.ErrorListener
    ) : Request<List<PhotosListItem>>(Method.GET, PHOTOS_ENDPOINT, errorListener) {
        override fun parseNetworkResponse(response: NetworkResponse?): Response<List<PhotosListItem>> =
            if (response?.statusCode == HttpURLConnection.HTTP_OK ||
                response?.statusCode == HttpURLConnection.HTTP_NOT_MODIFIED
            ) {
                String(response.data).run {
                    //convertendo o ArrayList
                    val listType = object : TypeToken<List<PhotosListItem>>() {}.type
                    Response.success(
                        Gson().fromJson(this, listType),
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            } else {
                Response.error(VolleyError())
            }

        override fun deliverResponse(response: List<PhotosListItem>?) {
            responseListener.onResponse(response)
        }

    }
}