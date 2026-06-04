package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.databinding.FragmentProductDetailBinding
import com.example.proyecto_ddm.entities.Cart
import com.example.proyecto_ddm.entities.CartItem
import com.example.proyecto_ddm.entities.Category
import com.example.proyecto_ddm.entities.Product
import com.example.proyecto_ddm.entities.State
import java.util.Locale

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"
        private const val ARG_CART_ID = "cart_id"
        private const val ARG_FROM_PURCHASES = "from_purchases"

        fun fromCatalog(productId: Int) =
            ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCT_ID, productId)
                    putBoolean(ARG_FROM_PURCHASES, false)
                }
            }

        fun fromPurchases(productId: Int, cartId: Int) =
            ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCT_ID, productId)
                    putInt(ARG_CART_ID, cartId)
                    putBoolean(ARG_FROM_PURCHASES, true)
                }
            }
    }

    private fun getProductById(id: Int): Product? {
        return sampleProducts().find { it.id == id }
    }

    private fun getCartById(id: Int): Cart? {
        return sampleCarts().find { it.id == id }
    }

    private fun sampleProducts() = listOf(
        Product(1, "The Legend of Zelda",
            Category(1, "Videojuego"),
            "Aventura de acción en mundo abierto con mecánicas únicas.",
            1299f),
        Product(2, "Control DualSense",
            Category(3, "Accesorio"),
            "Control inalámbrico con retroalimentación háptica.",
            1599f),
        Product(3, "PlayStation 5",
            Category(2, "Consola"),
            "Consola de última generación con SSD ultrarrápido.",
            12999f),
        Product(4, "FIFA 26",
            Category(1, "Videojuego"),
            "La nueva entrega del simulador de fútbol.",
            999f)
    )

    private fun sampleCarts() = listOf(
        Cart(id = 41, userId = 1,
            state = State(2, "entregado"),
            creationDate = "20/05/2026",
            completedDate = "28/05/2026",
            items = listOf(
                CartItem(sampleProducts()[0], 1),
                CartItem(sampleProducts()[1], 2)
            )
        ),
        Cart(id = 38, userId = 1,
            state = State(1, "proceso"),
            creationDate = "25/05/2026",
            completedDate = "05/06/2026",
            items = listOf(CartItem(sampleProducts()[2], 1))
        ),
        Cart(id = 35, userId = 1,
            state = State(0, "pendiente"),
            creationDate = "01/06/2026",
            completedDate = "10/06/2026",
            items = listOf(CartItem(sampleProducts()[3], 1))
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)

        val productId = arguments?.getInt(ARG_PRODUCT_ID, -1) ?: -1
        val cartId = arguments?.getInt(ARG_CART_ID, -1) ?: -1
        val fromPurchases = arguments?.getBoolean(ARG_FROM_PURCHASES, false) ?: false

        val product = getProductById(productId)
        val cart = if(fromPurchases) getCartById(cartId) else null
        val cartItem = cart?.items?.find { it.product.id == productId }

        if(product == null) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        setupHeader(fromPurchases, cart)
        populateProduct(product)

        if(fromPurchases && cartItem != null)
            populateOrderInfo(cart, cartItem)
    }

    private fun setupHeader(fromPurchases: Boolean, cart: Cart?) {
        if(fromPurchases && cart != null) {
            binding.flPurchases.visibility = View.VISIBLE
            binding.flCatalog.visibility = View.GONE
            binding.cardOrderInfo.visibility = View.VISIBLE

            val (bgRes, colorRes) = when(cart.state.name.lowercase(Locale.getDefault())) {
                "entregado" -> Pair(
                    R.drawable.bg_status_delivered,
                    R.color.gv_status_delivered
                )
                "proceso" -> Pair(
                    R.drawable.bg_status_in_progress,
                    R.color.gv_status_in_progress
                )
                else -> Pair(R.drawable.bg_status_pending, R.color.gv_accent)
            }

            binding.tvOrderStatus.setBackgroundResource(bgRes)
            binding.tvOrderStatus.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
            binding.tvOrderStatus.text = cart.state.name.replaceFirstChar { it.uppercase() }
        } else {
            binding.flPurchases.visibility = View.GONE
            binding.flCatalog.visibility = View.VISIBLE
            binding.cardOrderInfo.visibility = View.GONE
        }

        val backAction = View.OnClickListener {
            (requireActivity() as? MainActivity)?.showNavBar()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.fabBackPurchases.setOnClickListener(backAction)
        binding.fabBackCatalog.setOnClickListener(backAction)
    }

    private fun populateProduct(product: Product) {
        binding.tvDetailName.text = product.name
        binding.tvDetailCategory.text = product.category.name
        binding.tvDetailDescription.text = product.description
        binding.tvDetailPrice.text = "$${String.format("%,.2f", product.price)}"

        if(!product.img.isNullOrEmpty()) {
            binding.ivDetailProduct.setImageURI(product.img.toUri())
            binding.ivDetailProduct.clearColorFilter()
        } else {
            val iconRes = when (product.category.name.lowercase(Locale.getDefault())) {
                "videojuego" -> R.drawable.ic_outline_game_24
                "consola"    -> R.drawable.ic_outline_console_24
                "accesorio"  -> R.drawable.ic_outline_accessory_24
                else         -> R.drawable.ic_baseline_home_24
            }

            binding.ivDetailProduct.setImageResource(iconRes)
        }
    }

    private fun populateOrderInfo(cart: Cart, cartItem: CartItem) {
        binding.tvDetailOrderId.text = "#GV-${cart.id.toString().padStart(4, '0')}"
        binding.tvDetailQty.text = "x${cartItem.quantity}"
        binding.tvDetailDate.text = cart.completedDate ?: "No disponible"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}