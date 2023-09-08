package com.example.fuelpriceindia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    lateinit var stateAutoCompleteText : AutoCompleteTextView
    lateinit var cityAutoCompleteText : AutoCompleteTextView

    lateinit var petrolPrice : TextView
    lateinit var dieselPrice : TextView
    lateinit var cngPrice : TextView
    lateinit var lpgPrice : TextView
    lateinit var todayDate : TextView

    val fuelPriceAPI = FuelPriceAPI()

    var api_key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        api_key = resources.getString(R.string.rapidapi_key)

        todayDate = findViewById(R.id.todayDate)
        todayDate.text = "Today's Date: ${LocalDate.now()}"

        stateAutoCompleteText = findViewById(R.id.autoCompleteTextView)
        cityAutoCompleteText = findViewById(R.id.autoCompleteTextView2)

        val statesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.states))
        stateAutoCompleteText.setAdapter(statesAdapter)

        stateAutoCompleteText.setOnItemClickListener { _, _, position, _ ->
            val state = statesAdapter.getItem(position).toString().lowercase()
            fuelPriceAPI.getCities(api_key, state) { cities ->
                val citiesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
                cityAutoCompleteText.setAdapter(citiesAdapter)
            }
        }
    }

    fun checkPrice(view: View) {
        val state = stateAutoCompleteText.text.toString()
        val city = cityAutoCompleteText.text.toString()

        petrolPrice = findViewById(R.id.petrolPrice)
        dieselPrice = findViewById(R.id.dieselPrice)
        cngPrice = findViewById(R.id.cngPrice)
        lpgPrice = findViewById(R.id.lpgPrice)

        fuelPriceAPI.getFuelPrices(api_key, state.lowercase(), city.lowercase()) { petrolPrice, dieselPrice, cngPrice, lpgPrice ->
            this.petrolPrice.text = petrolPrice
            this.dieselPrice.text = dieselPrice
            this.cngPrice.text = cngPrice
            this.lpgPrice.text = lpgPrice
        }

    }
}