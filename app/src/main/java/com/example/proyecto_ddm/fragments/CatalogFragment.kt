package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.ProductAdapter
import com.example.proyecto_ddm.databinding.FragmentCatalogBinding
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.Product

class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private lateinit var adapter: ProductAdapter
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val items: List<Product> = buildList {
        add(Product(
            1, "The Legend of Zelda",
            Category(1, "Videojuego"),
            "Aventura de acción en mundo abierto.",
            1299f
        ))
        add(Product(
            2, "Control DualSense",
            Category(3, "Accesorio"),
            "Control inalámbrico para PS5.",
            1599f
        ))
        add(Product(
            3, "PlayStation 5",
            Category(2, "Consola"),
            "Consola de última generación.",
            12999f
        ))
        add(Product(
            4, "FIFA 26",
            Category(1, "Videojuego"),
            "La nueva entrega del simulador de fútbol.",
            999f
        ))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCatalogBinding.bind(view)

        setupRecyclerView()
        setupItems(items)
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(mutableListOf()) { product ->
            val fragment = ProductDetailFragment.fromCatalog(
                productId = product.id
            )
            (requireActivity() as MainActivity).navigateToDetail(fragment)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CatalogFragment.adapter
            setHasFixedSize(false)
        }
    }

    private fun setupItems(items: List<Product>) {
        adapter.updateList(items)
        val empty = items.isEmpty()
        binding.rvProducts.visibility = if (empty) View.GONE  else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}