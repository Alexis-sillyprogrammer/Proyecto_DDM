package com.example.proyecto_ddm.adapters

import android.graphics.BitmapFactory
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.models.Product
import com.google.android.material.button.MaterialButton
import java.io.File
import java.util.Locale

class ProductAdapter(
    private var productList: MutableList<Product>,
    private val onViewItem: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit,
    private val onToggleFavorites: (Product, Boolean) -> Unit,
    private var favoriteIds: Set<Int> = emptySet()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val btnAdd: MaterialButton = itemView.findViewById(R.id.btnAdd)
        val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductCategory.text = product.category.name
            tvProductPrice.text = String.format("$%.2f", product.price)

            var loaded = false
            if(!product.img.isNullOrEmpty()) {
                val imgFile = File(product.img)
                if(imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    if(bitmap != null) {
                        ivProduct.setImageBitmap(bitmap)
                        ivProduct.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                outline.setRoundRect(0, 0, view.width,
                                    view.height + 24, 24f)
                            }
                        }

                        ivProduct.clipToOutline = true
                        loaded = true
                    }
                }
            }

            if(!loaded) {
                val iconRes = when(product.category.name.lowercase(Locale.getDefault())) {
                    "videojuego" -> R.drawable.ic_outline_game_24
                    "consola" -> R.drawable.ic_outline_console_24
                    "accesorio" -> R.drawable.ic_outline_accessory_24
                    else -> R.drawable.ic_baseline_home_24
                }

                ivProduct.setImageResource(iconRes)
                ivProduct.clipToOutline = false
            }

            val isFav = favoriteIds.contains(product.id)
            updateFavoriteIcon(isFav)
            btnFavorite.setOnClickListener {
                val newState = !favoriteIds.contains(product.id)
                favoriteIds = if(newState) favoriteIds + product.id
                else favoriteIds - product.id
                updateFavoriteIcon(newState)
                onToggleFavorites(product, newState)
            }

            btnAdd.setOnClickListener { onAddToCart(product) }
            itemView.setOnClickListener { onViewItem(product) }
        }

        private fun updateFavoriteIcon(isFav: Boolean) {
            val color = if(isFav) R.color.gv_accent else R.color.gv_icon_inactive
            btnFavorite.setColorFilter(
                ContextCompat.getColor(itemView.context, color)
            )

            btnFavorite.setImageResource(
                if(isFav) R.drawable.ic_favorite_24
                else R.drawable.ic_favorite_border_24
            )
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

    fun updateList(newItems: List<Product>, newFavoriteIds: Set<Int> = favoriteIds) {
        val oldFavoriteIds = favoriteIds
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = productList.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) = productList[o].id == newItems[n].id
            override fun areContentsTheSame(o: Int, n: Int): Boolean {
                val pOld = productList[o]
                val pNew = newItems[n]
                val wasFav = oldFavoriteIds.contains(pOld.id)
                val isFav = newFavoriteIds.contains(pNew.id)
                return pOld == pNew && wasFav == isFav
            }
        }

        val result = DiffUtil.calculateDiff(diff)
        this.favoriteIds = newFavoriteIds
        this.productList.clear()
        this.productList.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    fun updateFavorites(ids: Set<Int>) {
        val oldIds = favoriteIds
        this.favoriteIds = ids
        for (i in productList.indices) {
            val productId = productList[i].id
            val wasFavorite = oldIds.contains(productId)
            val isFavorite = ids.contains(productId)

            if (wasFavorite != isFavorite) {
                notifyItemChanged(i)
            }
        }
    }
}