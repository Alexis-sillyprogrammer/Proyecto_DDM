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
                if(isAdmin)
                    Snackbar.make(
                        binding.root,
                        "Los administradores no pueden agregar productos al carrito",
                        Snackbar.LENGTH_SHORT
                    ).show()
                else addToCart(product)
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
            adapter.updateList(products)

            val empty = products.isEmpty()
            binding.rvProducts.visibility = if(empty) View.GONE  else View.VISIBLE
            binding.llEmpty.visibility = if(empty) View.VISIBLE else View.GONE
        }
    }

    private fun addToCart(product: com.example.proyecto_ddm.models.Product) {
        if(userId == -1) {
            Snackbar.make(
                binding.root,
                "Error de sesión",
                Snackbar.LENGTH_SHORT
            ).show()

            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val success = repo.addToCart(userId, product)
            val msg = if(success) "«${product.name}» agregado al carrito" else "Error al agregar al carrito"
            Snackbar.make(
                binding.root,
                msg,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}