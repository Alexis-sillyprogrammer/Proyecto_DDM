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
import kotlinx.coroutines.launch

class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private lateinit var repo: GameVaultRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCatalogBinding.bind(view)
        repo = GameVaultRepository(requireContext())

        setupRecyclerView()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(mutableListOf()) { product ->
            val fragment = ProductDetailFragment.fromCatalog(product.id)
            (requireActivity() as MainActivity).navigateToDetail(fragment)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CatalogFragment.adapter
            setHasFixedSize(false)
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val isAdmin = requireActivity().intent.getBooleanExtra("IS_ADMIN", false)
            val products = if(isAdmin) repo.getAllProducts() else repo.getAvailableProducts()

            adapter.updateList(products)

            val empty = products.isEmpty()
            binding.rvProducts.visibility = if(empty) View.GONE else View.VISIBLE
            binding.llEmpty.visibility = if(empty) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}