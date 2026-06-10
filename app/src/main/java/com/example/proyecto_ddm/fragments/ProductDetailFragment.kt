package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentProductDetailBinding
import com.example.proyecto_ddm.models.Cart
import com.example.proyecto_ddm.models.CartItem
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.Product
import com.example.proyecto_ddm.models.State
import kotlinx.coroutines.launch
import java.util.Locale

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)
        repo = GameVaultRepository(requireContext())

        val productId = arguments?.getInt(ARG_PRODUCT_ID, -1) ?: -1
        val cartId = arguments?.getInt(ARG_CART_ID, -1) ?: -1
        val fromPurchases = arguments?.getBoolean(ARG_FROM_PURCHASES, false) ?: false

        if(productId == -1) {
            navigateBack()
            return
        }

        setupBackButtons()
        loadData(productId, cartId, fromPurchases)
    }

    private fun setupBackButtons() {
        val backAction = View.OnClickListener {
            (requireActivity() as? MainActivity)?.showNavBar()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.fabBackPurchases.setOnClickListener(backAction)
        binding.fabBackCatalog.setOnClickListener(backAction)
    }

    private fun loadData(productId: Int, cartId: Int, fromPurchases: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val product = repo.getProductById(productId)

            if(product == null) {
                navigateBack()
                return@launch
            }

            if(fromPurchases && cartId != -1) {
                val flatItems = repo.getCartItemsByCartId(cartId)
                val cartModel = repo.getCartModelById(cartId)
                val cartItem  = flatItems.find { it.product.id == productId }

                setupHeader(fromPurchases = true, cart = cartModel)
                populateProduct(product)

                if(cartModel != null && cartItem != null) populateOrderInfo(cartModel, cartItem)
            } else {
                setupHeader(fromPurchases = false, cart = null)
                populateProduct(product)
            }
        }
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
                "consola" -> R.drawable.ic_outline_console_24
                "accesorio" -> R.drawable.ic_outline_accessory_24
                else -> R.drawable.ic_baseline_home_24
            }

            binding.ivDetailProduct.setImageResource(iconRes)
        }
    }

    private fun populateOrderInfo(cart: Cart, cartItem: CartItem) {
        binding.tvDetailOrderId.text = "#GV-${cart.id.toString().padStart(4, '0')}"
        binding.tvDetailQty.text = "x${cartItem.quantity}"
        binding.tvDetailDate.text = cart.completedDate ?: "No disponible"
    }

    private fun navigateBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}