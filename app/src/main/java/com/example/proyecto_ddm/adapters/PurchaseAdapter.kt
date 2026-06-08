package com.example.proyecto_ddm.adapters

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.models.FlatPurchaseItem
import java.util.Locale

class PurchaseAdapter(
    private var items: MutableList<FlatPurchaseItem>,
    private val onViewItem: (FlatPurchaseItem) -> Unit
) : RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder>() {
    inner class PurchaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductOrder)
        val tvCategoryQty: TextView = itemView.findViewById(R.id.tvCategoryQty)
        val tvDateOrder: TextView = itemView.findViewById(R.id.tvDateOrder)
        val tvTotalOrder: TextView = itemView.findViewById(R.id.tvTotalOrder)
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProductOrder)
        val ivDateIcon: ImageView = itemView.findViewById(R.id.ivDateIcon)

        fun bind(flatItem: FlatPurchaseItem) {
            val cart = flatItem.cart
            val item = flatItem.cartItem
            val product = item.product

            tvOrderId.text = "#GV-${cart.id.toString().padStart(4, '0')}"
            tvProductName.text = product.name
            tvCategoryQty.text = "${product.category.name} · x${item.quantity}"
            tvTotalOrder.text = "$${String.format("%,.2f", item.subtotal)}"
            tvDateOrder.text = flatItem.dateLabel

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

            val (bgRes, colorRes, iconRes) = when(cart.state.name) {
                "entregado" -> Triple(
                    R.drawable.bg_status_delivered,
                    R.color.gv_status_delivered,
                    R.drawable.ic_outline_check_circle_24
                )
                "proceso" -> Triple(
                    R.drawable.bg_status_in_progress,
                    R.color.gv_status_in_progress,
                    R.drawable.ic_outline_calendar_24
                )
                else -> Triple(
                    R.drawable.bg_status_pending,
                    R.color.gv_accent,
                    R.drawable.ic_outline_calendar_24
                )
            }

            tvOrderStatus.text = cart.state.name.replaceFirstChar { it.uppercase() }
            tvOrderStatus.setBackgroundResource(bgRes)
            tvOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, colorRes))

            ivDateIcon.setImageResource(iconRes)

            itemView.setOnClickListener {
                onViewItem(flatItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase, parent, false)

        return PurchaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<FlatPurchaseItem>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) =
                items[o].cart.id == newItems[n].cart.id &&
                        items[o].cartItem.product.id == newItems[n].cartItem.product.id
            override fun areContentsTheSame(o: Int, n: Int) =
                items[o] == newItems[n]
        }
        val result = DiffUtil.calculateDiff(diff)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}