package com.example.proyecto_ddm.adapters

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.models.Product
import com.google.android.material.button.MaterialButton
import java.util.Locale

class ProductAdapter(
    private var productList: MutableList<Product>,
    private val onViewItem: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val btnAdd: MaterialButton = itemView.findViewById(R.id.btnAdd)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductCategory.text = product.category.name
            tvProductPrice.text = String.format("$%.2f", product.price)

            if(!product.img.isNullOrEmpty()) {
                ivProduct.setImageURI(product.img.toUri())
                ivProduct.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val radius = 24f
                        outline.setRoundRect(0, 0, view.width, view.height + radius.toInt(), radius)
                    }
                }

                ivProduct.clipToOutline = true
            }
            else {
                val iconRes = when(product.category.name.lowercase(Locale.getDefault())) {
                    "videojuego" -> R.drawable.ic_outline_game_24
                    "consola" -> R.drawable.ic_outline_console_24
                    "accesorio" -> R.drawable.ic_outline_accessory_24
                    else -> R.drawable.ic_baseline_home_24
                }

                ivProduct.setImageResource(iconRes)
                ivProduct.clipToOutline = false
            }

            btnAdd.setOnClickListener {
                onAddToCart(product)
            }

            itemView.setOnClickListener {
                onViewItem(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount() = productList.size

    fun updateList(newItems: List<Product>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = productList.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) =
                productList[o].id == newItems[n].id

            override fun areContentsTheSame(o: Int, n: Int) =
                productList[o] == newItems[n]
        }
        val result = DiffUtil.calculateDiff(diff)
        productList.clear()
        productList.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}