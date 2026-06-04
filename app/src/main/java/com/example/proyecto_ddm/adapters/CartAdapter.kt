package com.example.proyecto_ddm.adapters

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.entities.CartItem
import java.util.Locale

class CartAdapter(
    private var items: MutableList<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val productPrice: TextView = itemView.findViewById(R.id.tvUnitPrice)
        val productImage: ImageView = itemView.findViewById(R.id.ivProduct)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val quantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val subtotalItem: TextView = itemView.findViewById(R.id.tvSubtotalItem)
        val deleteItem: FrameLayout = itemView.findViewById(R.id.btnDeleteItem)

        fun bind(item: CartItem) {
            val context = itemView.context
            val product = item.product

            productName.text = product.name
            productCategory.text = product.category.name
            productPrice.text = context.getString(R.string.price_per_unit_format, product.price)
            quantity.text = item.quantity.toString()
            subtotalItem.text = context.getString(R.string.subtotal_format, item.subtotal)

            if (!product.img.isNullOrEmpty()) {
                productImage.setImageURI(product.img.toUri())
                productImage.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val radius = 24f
                        outline.setRoundRect(0, 0, view.width, view.height + radius.toInt(), radius)
                    }
                }
                productImage.clipToOutline = true
            } else {
                val placeholder = when (product.category.name.lowercase(Locale.getDefault())) {
                    "videojuego" -> R.drawable.ic_outline_game_24
                    "consola" -> R.drawable.ic_outline_console_24
                    "accesorio" -> R.drawable.ic_outline_accessory_24
                    else -> R.drawable.ic_baseline_home_24
                }
                productImage.setImageResource(placeholder)
            }

            btnIncrease.setOnClickListener {
                if (item.quantity < 99) {
                    item.quantity++
                    quantity.text = item.quantity.toString()
                    subtotalItem.text = context.getString(R.string.subtotal_format, item.subtotal)
                    onQuantityChanged(item, item.quantity)
                }
            }

            btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    quantity.text = item.quantity.toString()
                    subtotalItem.text = context.getString(R.string.subtotal_format, item.subtotal)
                    onQuantityChanged(item, item.quantity)
                }
            }

            deleteItem.setOnClickListener {
                onDeleteItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newCartItems: List<CartItem>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newCartItems.size
            override fun areItemsTheSame(o: Int, n: Int) =
                items[o].product.id == newCartItems[n].product.id

            override fun areContentsTheSame(o: Int, n: Int) =
                items[o] == newCartItems[n]
        }

        val result = DiffUtil.calculateDiff(diff)
        items.clear()
        items.addAll(newCartItems)
        result.dispatchUpdatesTo(this)
    }

    fun deleteItem(item: CartItem) {
        val pos = items.indexOfFirst { it.product.id == item.product.id }

        if(pos != -1) {
            items.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun emptyCart() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getItems(): List<CartItem> = items.toList()
}