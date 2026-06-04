package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.PurchaseAdapter
import com.example.proyecto_ddm.databinding.FragmentPurchasesBinding
import com.example.proyecto_ddm.entities.Cart
import com.example.proyecto_ddm.entities.State
import com.example.proyecto_ddm.entities.Category
import com.example.proyecto_ddm.entities.CartItem
import com.example.proyecto_ddm.entities.Product
import com.example.proyecto_ddm.entities.FlatPurchaseItem

class PurchasesFragment : Fragment(R.layout.fragment_purchases) {
    private lateinit var adapter: PurchaseAdapter
    private var _binding: FragmentPurchasesBinding? = null
    private val binding get() = _binding!!
    private val items: List<FlatPurchaseItem> = buildList {
        val carrito1 = Cart(
            id = 41,
            userId = 1,
            state = State(2, "entregado"),
            creationDate = "20/05/2026",
            completedDate = "28/05/2026"
        )
        add(FlatPurchaseItem(carrito1, CartItem(
            product = Product(1, "The Legend of Zelda",
                Category(1, "Videojuego"),
                "Aventura de acción en mundo abierto.",
                1299f),
            quantity = 1
        )))
        add(FlatPurchaseItem(carrito1, CartItem(
            product = Product(2, "Control DualSense",
                Category(3, "Accesorio"),
                "Control inalámbrico para PS5.",
                1599f),
            quantity = 2
        )))

        val carrito2 = Cart(
            id = 38,
            userId = 1,
            state = State(1, "proceso"),
            creationDate = "25/05/2026",
            completedDate = "05/06/2026"
        )
        add(FlatPurchaseItem(carrito2, CartItem(
            product = Product(3, "PlayStation 5",
                Category(2, "Consola"),
                "Consola de última generación.",
                12999f),
            quantity = 1
        )))

        val carrito3 = Cart(
            id = 35,
            userId = 1,
            state = State(0, "pendiente"),
            creationDate = "01/06/2026",
            completedDate = "10/06/2026"
        )
        add(FlatPurchaseItem(carrito3, CartItem(
            product = Product(4, "FIFA 26",
                Category(1, "Videojuego"),
                "La nueva entrega del simulador de fútbol.",
                999f),
            quantity = 1
        )))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPurchasesBinding.bind(view)

        setupRecyclerView()
        setupFilters()
        setupItems(items)
    }

    private fun setupRecyclerView() {
        adapter = PurchaseAdapter(mutableListOf()) { flatItem ->
            val fragment = ProductDetailFragment.fromPurchases(
                productId = flatItem.cartItem.product.id,
                cartId = flatItem.cart.id
            )
            (requireActivity() as MainActivity).navigateToDetail(fragment)
        }

        binding.rvPurchases.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PurchasesFragment.adapter
            setHasFixedSize(false)
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filtered = when (checkedIds.firstOrNull()) {
                R.id.chipDelivered -> items.filter {
                    it.statusName == "entregado"
                }
                R.id.chipInProcess  -> items.filter {
                    it.statusName == "proceso"
                }
                R.id.chipPending -> items.filter {
                    it.statusName == "pendiente"
                }
                else -> items
            }
            setupItems(filtered)
        }
    }

    private fun setupItems(items: List<FlatPurchaseItem>) {
        adapter.updateList(items)
        binding.tvOrderCount.text =
            "${items.size} pedido${if (items.size != 1) "s" else ""}"

        val empty = items.isEmpty()
        binding.rvPurchases.visibility = if (empty) View.GONE  else View.VISIBLE
        binding.llEmpty.visibility = if (empty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}