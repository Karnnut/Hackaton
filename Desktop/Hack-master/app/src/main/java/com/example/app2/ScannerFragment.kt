package com.example.app2

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import cphack.testkotlin.controller.CacheAPI
import cphack.testkotlin.controller.FetchUcpAPI
import cphack.testkotlin.model.Product
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

private const val CAMERA_CODE = 101
class ScannerFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    private val cacheAPI : CacheAPI = CacheAPI(60*30)
    private val es : ExecutorService = Executors.newFixedThreadPool(2)
    private lateinit var codeScanner: CodeScanner


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var m = MainActivity()
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        val ubc: String
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                val ubc: String = it.text
                println("eeeeeehthyrhrrrtrhtrrgtrgegteyhheheyheyhtg  "+ubc)

                if(cacheAPI.get(ubc,"en") != null){
                    val product = cacheAPI.get(ubc,"en")
                    println(product)
                    if (product != null) {
                        m.customerCart.add(product)
                    }
                }else{
                    try {
                        val fetUcpTask :FetchUcpAPI = FetchUcpAPI(ubc,"en")
                        val productFuture : Future<Product> = es.submit(fetUcpTask)
                        val product : Product = productFuture.get()
                        if(product.name == "-1" || product.sku == "-1"){
                            println("Data doesn't exist")
                        }else {
                            m.customerCart.add(product)
                            cacheAPI.put(ubc, product, "en")
                        }
                        println("Hello")
                        var c = m.customerCart
                        println(c[0])
                    }catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {

        codeScanner.releaseResources()
        super.onPause()
    }


}

