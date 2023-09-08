package com.example.fuelpriceindia

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import org.json.JSONException
import org.json.JSONObject

public class FuelPriceAPI {
    val client = okhttp3.OkHttpClient()

    fun getFuelPrices(api_key: String, state: String, city: String,callback: (String, String, String, String) -> Unit) {

        val url =
            "https://daily-petrol-diesel-lpg-cng-fuel-prices-in-india.p.rapidapi.com/v1/fuel-prices/today/india/$state/$city"

        val request = okhttp3.Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", api_key)
            .addHeader(
                "x-rapidapi-host",
                "daily-petrol-diesel-lpg-cng-fuel-prices-in-india.p.rapidapi.com"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                Log.d("TAG", "onResponse: $body")

                try {
                    val jsonObject = JSONObject(body)

                    Log.d("TAG", "JSON Object: $jsonObject")

                    if (jsonObject.has("fuel")) {
                        val fuel = jsonObject.getJSONObject("fuel")

                        val petrol = fuel.optJSONObject("petrol") ?: JSONObject()
                        val diesel = fuel.optJSONObject("diesel") ?: JSONObject()
                        val cng = fuel.optJSONObject("cng") ?: JSONObject()
                        val lpg = fuel.optJSONObject("lpg") ?: JSONObject()

                        val petrolPrice = petrol.optString("retailPrice", "N/A")
                        val dieselPrice = diesel.optString("retailPrice", "N/A")
                        val cngPrice = cng.optString("retailPrice", "N/A")
                        val lpgPrice = lpg.optString("retailPrice", "N/A")

                        Handler(Looper.getMainLooper()).post {
                            callback(petrolPrice, dieselPrice, cngPrice, lpgPrice)
                        }
                    } else {
                        Log.e("TAGX", "JSON response is missing required keys.")
                    }
                } catch (e: JSONException) {
                    Log.e("TAGX", "Error parsing JSON: ${e.message}")
                }
            }

        })
    }

    fun getCities(api_key: String, state: String, callback: (ArrayList<String>) -> Unit) {
        val url = "https://daily-petrol-diesel-lpg-cng-fuel-prices-in-india.p.rapidapi.com/v1/fuel-prices/today/india/$state/cities"

        val request = okhttp3.Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", api_key)
            .addHeader(
                "x-rapidapi-host",
                "daily-petrol-diesel-lpg-cng-fuel-prices-in-india.p.rapidapi.com"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                Log.d("TAG", "onResponse: $body")

                try {
                    val jsonObject = JSONObject(body)

                    Log.d("TAG", "JSON Object: $jsonObject")

                    if (jsonObject.has("cityPrices")) {
                        val cityPrices = jsonObject.getJSONArray("cityPrices")

                        val citiesList = ArrayList<String>()

                        for (i in 0 until cityPrices.length()) {
                            val cityObject = cityPrices.getJSONObject(i)
                            val cityId = cityObject.getString("cityId")
                            citiesList.add(cityId.capitalize())
                        }

                        Handler(Looper.getMainLooper()).post {
                            callback(citiesList)
                        }
                    } else {
                        Log.e("TAG", "JSON response is missing required keys.")
                    }
                } catch (e: JSONException) {
                    Log.e("TAG", "Error parsing JSON: ${e.message}")
                }
            }

        })
    }


}
