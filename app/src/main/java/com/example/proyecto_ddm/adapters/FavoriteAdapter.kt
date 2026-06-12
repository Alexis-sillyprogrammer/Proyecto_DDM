package com.example.proyecto_ddm.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.models.Product
import java.io.File
import java.util.Locale

class FavoriteAdapter(
    private var items: MutableList<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavViewHolder>() {
    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivFavProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvFavName)

        fun bind(product: Product) {
            tvName.text = product.name
            var loaded = false
            if(!product.img.isNullOrEmpty()) {
                val file = File(product.img)
                val bitmap = if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                if(bitmap != null) {
                    ivProduct.setImageBitmap(bitmap)
                    ivProduct.clearColorFilter()
                    loaded = true
                }
            }
            if(!loaded) {
                val icon = when (product.category.name.lowercase(Locale.getDefault())) {
                    "videojuego" -> R.drawable.ic_outline_game_24
                    "consola" -> R.drawable.ic_outline_console_24
                    "accesorio" -> R.drawable.ic_outline_accessory_24
                    else -> R.drawable.ic_baseline_home_24
                }

                ivProduct.setImageResource(icon)
                ivProduct.scaleType = ImageView.ScaleType.CENTER
            }

            itemView.setOnClickListener { onAddToCart(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FavViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite_chip, parent, false)
        )

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Product>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) =
                items[o].id == newItems[n].id

            override fun areContentsTheSame(o: Int, n: Int) =
                items[o] == newItems[n]
        }

        val result = DiffUtil.calculateDiff(diff)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}