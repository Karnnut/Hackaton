package com.example.app2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cphack.testkotlin.controller.FetchProductAPI
import cphack.testkotlin.controller.GenerateBill
import cphack.testkotlin.model.Product
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val executor : ExecutorService = Executors.newFixedThreadPool(2)



    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        try{
            val fetchProductTask : FetchProductAPI = FetchProductAPI("250090","en")
            val productFuture : Future<Product> = executor.submit(fetchProductTask)
            val product : Product = productFuture.get()

            println(product)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var m = (requireActivity() as MainActivity)
        var c = m.customerCart
        var G = GenerateBill(c)
        var bill : String = G.billSummary
        var sum_bill : Double = G.billSumFullPrice


        val textView7 = view.findViewById<TextView>(R.id.textView7)
        val textView8 = view.findViewById<TextView>(R.id.textView8)


        textView7.text = bill
        textView8.text = "Total Price is : " + sum_bill
    }

}