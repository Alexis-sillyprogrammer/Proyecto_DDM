package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.ProductAdapter
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentCatalogBinding
import com.example.proyecto_ddm.models.Product
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private lateinit var repo: GameVaultRepository
    private var isAdmin: Boolean = false
    private var userId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCatalogBinding.bind(view)
        repo = GameVaultRepository(requireContext())
        isAdmin = requireActivity().intent.getBooleanExtra("IS_ADMIN", false)
        userId = requireActivity().intent.getIntExtra("USER_ID", -1)

        setupRecyclerView()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        if(::adapter.isInitialized) loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            productList = mutableListOf(),
            onViewItem = { product ->
                val fragment = ProductDetailFragment.fromCatalog(product.id)
                (requireActivity() as MainActivity).navigateToDetail(fragment)
            },
            onAddToCart = { product ->
                if(isAdmin) showSnackbar("Los administradores no pueden agregar al carrito")
                else addToCart(product)
            },
            onToggleFavorites = { product, newState ->
                toggleFavorite(product, newState)
            }
        )

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CatalogFragment.adapter
            setHasFixedSize(false)
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val products = if(isAdmin) repo.getAllProducts() else repo.getAvailableProducts()
            val favIds = if(userId != -1) repo.getFavoriteProducts(userId).map { it.id }.toSet() else emptySet()
            adapter.updateList(products)
            adapter.updateFavorites(favIds)

            val empty = products.isEmpty()
            binding.rvProducts.visibility = if(empty) View.GONE  else View.VISIBLE
            binding.llEmpty.visibility = if(empty) View.VISIBLE else View.GONE
        }
    }

    private fun addToCart(product: Product) {
        if(userId == -1) {
            showSnackbar("Error de sesión")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val success = repo.addToCart(userId, product)
            showSnackbar(if(success) "«${product.name}» agregado al carrito"
            else "Error al agregar al carrito")
        }
    }

    private fun toggleFavorite(product: Product, newState: Boolean) {
        if (userId == -1) return
        viewLifecycleOwner.lifecycleScope.launch {
            repo.toggleFavorite(userId, product.id)
            val msg = if(newState) "«${product.name}» agregado a favoritos"
            else "«${product.name}» eliminado de favoritos"
            showSnackbar(msg)
        }
    }

    private fun showSnackbar(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}