package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_PER_SAME_DAY_PICK_UP = 3.00

class CupcakeViewModel : ViewModel() {

    private val _quantity = MutableLiveData<Int>()
    val quantity : LiveData<Int> = _quantity

    private val _flavor = MutableLiveData<String>()
    val flavor : LiveData<String> = _flavor

    // Price of the order
    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _date = MutableLiveData<String>()
    val date : LiveData<String> = _date

    val dates = getDateOptions()

    init{
        resetOrder()
    }

    /**
     * Here [userQuantity] [userFlavor] and [selectedDate] are value that will be passed in each
     * method in the fragment depending on the user's choice
     */
    fun setQuantity(userQuantity : Int){
        _quantity.value = userQuantity
        updatePrice()
    }

    fun setFlavor(userFlavor : String){
        _flavor.value = userFlavor
    }

    fun setDate(selectedDate : String){
        _date.value = selectedDate
        updatePrice()
    }

  //Checks if there's a flavor
    fun hasNoFlavorSet() : Boolean{
        return _flavor.value.isNullOrEmpty()
    }

    /**Method that determines the date or time and formats it**/
    private fun getDateOptions() : List<String>{
        val fourDates = mutableListOf<String>()

        val formatter = SimpleDateFormat("E MMM dd", Locale.getDefault())

        val calendar = Calendar.getInstance()

        repeat(4){
            fourDates.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return fourDates
    }

    //Updates the price
    private fun updatePrice(){
        var newPrice = (_quantity.value?:0) * PRICE_PER_CUPCAKE

        if (_date.value == dates[0]){
            newPrice += PRICE_PER_SAME_DAY_PICK_UP
        }
        _price.value = newPrice
    }

    //Checks if the user enters valid information
    fun userInputInvalid(input : String) : Boolean = input.isNullOrBlank()


    //Reinitialize order
    fun resetOrder(){
        _flavor.value = ""
        _date.value = dates[0]
        _quantity.value = 0
        _price.value = 0.0
    }


}