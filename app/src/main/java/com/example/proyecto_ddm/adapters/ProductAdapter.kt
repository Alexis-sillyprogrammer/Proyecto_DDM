package com.example.proyecto_ddm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.models.Product
import com.google.android.material.button.MaterialButton

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductCategory: TextView = view.findViewById(R.id.tvProductCategory)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val btnAdd: MaterialButton = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvProductName.text = product.name
        holder.tvProductCategory.text = product.category

        // Formatear el precio para que se vea como moneda
        holder.tvProductPrice.text = String.format("$%.2f", product.price)

        holder.btnAdd.setOnClickListener {
            // Aquí irá la lógica para agregar al carrito después
        }
    }

    override fun getItemCount() = productList.size
}