package com.myclient.cart

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myclient.R
import com.myclient.databinding.FragmentCartBinding
import com.myclient.entities.Product
import com.myclient.order.OrderActivity
import com.myclient.product.MainAux

class CartFragment : BottomSheetDialogFragment(), OnCartListener {

    private var binding: FragmentCartBinding? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var adapter: ProductCartAdapter

    private var totalPrice = 0.0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCartBinding.inflate(LayoutInflater.from(activity))
        binding?.let {
            val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
            bottomSheetDialog.setContentView(it.root)

            //instanciar las var
            bottomSheetBehavior = BottomSheetBehavior.from(it.root.parent as View)
            //manipular el comportamiento a traves del estado
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

   /*
 // para instanciarlo en un fragment
 val fragment = BottomSheetFragment()
 fragment.show(fragmentManager!!.beginTransaction(), BottomSheetFragment.TAG)
   */

            setupRecyclerView()
            setupButtons()

            getProducts()

            return bottomSheetDialog
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun setupRecyclerView() {
        binding?.let {
            adapter = ProductCartAdapter(mutableListOf(), this)

            it.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@CartFragment.adapter
            }

            /*(1..5).forEach {
                val product = Product(it.toString(), "Producto $it", "This product is $it",
                    "", it, 2.0*it)
                adapter.add(product)
            }*/
        }
    }

    //boton de cerrar del bottomSheet
    private fun setupButtons(){
        binding?.let {
            it.ibCancel.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            //extraer el pedido en una orden
            it.efab.setOnClickListener {
                requestOrder()
            }
        }
    }

    private fun getProducts(){
        (activity as? MainAux)?.getProductsCart()?.forEach {
            adapter.add(it)
        }
    }

    //cerrar el fragmento y limpiar el listado
    private fun requestOrder(){
        dismiss()  //quitar el fragment
        (activity as? MainAux)?.clearCart()
        startActivity(Intent(context, OrderActivity::class.java))
    }

    //volver a binding = null (buenas practicas)
    override fun onDestroyView() {
        (activity as? MainAux)?.updateTotal()  //para refrescar el total en la mainActivity
        super.onDestroyView()
        binding = null
    }

    //obtener las cantidades del carrito (para modif sus cantitades desde ahi)
    override fun setQuantity(product: Product) {
        adapter.update(product)
    }

    //actualizar el total del carrito en el fragment (lo detona el adapter)
    override fun showTotal(total: Double) {
        totalPrice = total
        binding?.let {
            it.tvTotal.text = getString(R.string.product_full_cart, total)
        }
    }
}