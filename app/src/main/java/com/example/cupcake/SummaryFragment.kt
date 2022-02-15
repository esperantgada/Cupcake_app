/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentSummaryBinding
import com.example.cupcake.model.CupcakeViewModel

/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
@Suppress("KDocUnresolvedReference")
class SummaryFragment : Fragment() {

    /**Using [activityViewModels] allows to share the viewModel in fragments**/
    private val shareViewModel : CupcakeViewModel by activityViewModels()


    // Binding object instance corresponding to the fragment_summary.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private  var binding: FragmentSummaryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = shareViewModel
            summaryFragment = this@SummaryFragment
        }

        //Observe the price changes
        shareViewModel.price.observe(viewLifecycleOwner){ newPrice ->
            binding?.total?.text = getString(R.string.subtotal_price, newPrice)
        }
    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     * Using [getQuantityString] method with [plurals] string resource allows us to handle the [singular]
     * or [plural] of [cupcake]
     */
    fun sendOrder() {
        if (binding?.ownerName?.text.isNullOrBlank()
            || binding?.ownerPhone?.text.isNullOrBlank() || binding?.ownerLocation?.text.isNullOrBlank()){
            setError(true)
        }else{
            setError(false)
            val numberOfCupcakes = shareViewModel.quantity.value ?: 0
            val orderSummary = getString(
                R.string.order_details,
                resources.getQuantityString(R.plurals.cupcakes, numberOfCupcakes, numberOfCupcakes),
                shareViewModel.flavor.value.toString(),
                shareViewModel.date.value.toString(),
                shareViewModel.price.value.toString(),
                binding?.ownerName?.text.toString(),
                binding?.ownerPhone?.text.toString(),
                binding?.ownerLocation?.text.toString()
            )


            val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.new_cupcake_order))
                .putExtra(Intent.EXTRA_TEXT, orderSummary)


            //Checks if there's an app that can handle the intent
            if (activity?.packageManager?.resolveActivity(intent, 0) != null){
                startActivity(intent)
            }
        }
    }

    /**
     * Cancels an order and navigates to the home fragment [StartFragment]
     */
    fun cancelOrder(){
        shareViewModel.resetOrder()
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
    }

    /**
     * Handles error when user's inputs are invalid
     */
    private fun setError(error : Boolean) : Boolean{
        var answer = true
       if (error){
           when {
               shareViewModel.userInputInvalid(binding?.ownerName?.text.toString()) -> {
                   binding?.name?.isErrorEnabled = true
                   binding?.name?.error = getString(R.string.name)
               }
               shareViewModel.userInputInvalid(binding?.ownerPhone?.text.toString()) -> {
                   binding?.phone?.isErrorEnabled = true
                   binding?.phone?.error = getString(R.string.phone)
               }
               shareViewModel.userInputInvalid(binding?.ownerLocation?.text.toString()) -> {
                   binding?.location?.isErrorEnabled = true
                   binding?.location?.error = getString(R.string.location)
               }
           }

        } else {
            binding?.name?.isErrorEnabled = false
            binding?.phone?.isErrorEnabled = false
            binding?.location?.isErrorEnabled = false

           answer = false

        }

        return answer
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}