@file:Suppress("KDocUnresolvedReference")

package com.example.cupcake

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cupcake.model.CupcakeViewModel
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ViewModelTest {

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Here, we're testing [setQuantity] method in the [CupcakeViewModel] class by checking the value
     * of [quantity] liveData object
     */
    @Test
    fun quantity_twelve_cupcakes(){
        val viewModel = CupcakeViewModel()
        viewModel.quantity.observeForever {  }
        viewModel.setQuantity(12)

        assertEquals(12, viewModel.quantity.value)
    }

    /**
     * Test for [price] method in the [CupcakeViewModel] class
     */
    @Test
    fun price_of_twelve_cupcakes(){
        val viewModel = CupcakeViewModel()
        viewModel.price.observeForever {  }
        viewModel.setQuantity(12)
        assertEquals("$27.00", viewModel.price.value)
    }

}