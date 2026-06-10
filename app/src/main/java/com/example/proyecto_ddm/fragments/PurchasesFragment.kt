package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.PurchaseAdapter
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentPurchasesBinding
import com.example.proyecto_ddm.models.Cart
import com.example.proyecto_ddm.models.State
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.CartItem
import com.example.proyecto_ddm.models.Product
import com.example.proyecto_ddm.models.FlatPurchaseItem
import kotlinx.coroutines.launch

class PurchasesFragment : Fragment(R.layout.fragment_purchases) {
    private var _binding: FragmentPurchasesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PurchaseAdapter
    private lateinit var repo: GameVaultRepository
    private var userId: Int = -1
    private var allItems: List<FlatPurchaseItem> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPurchasesBinding.bind(view)
        repo = GameVaultRepository(requireContext())
        userId = requireActivity().intent.getIntExtra("USER_ID", -1)

        setupRecyclerView()
        setupFilters()
        loadPurchases()
    }

    override fun onResume() {
        super.onResume()
        if(::adapter.isInitialized) loadPurchases()
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

    private fun loadPurchases() {
        if (userId == -1) return

        viewLifecycleOwner.lifecycleScope.launch {
            allItems = repo.getCompletedCarts(userId)

            val checkedId = binding.chipGroupFilter.checkedChipId
            applyFilter(checkedId)
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            applyFilter(checkedIds.firstOrNull() ?: R.id.chipAll)
        }
    }

    private fun applyFilter(chipId: Int?) {
        val filtered = when (chipId) {
            R.id.chipDelivered -> allItems.filter { it.statusName == "completado" }
            R.id.chipInProcess -> allItems.filter { it.statusName == "proceso"    }
            R.id.chipPending -> allItems.filter { it.statusName == "pendiente"  }
            else -> allItems
        }

        displayItems(filtered)
    }

    private fun displayItems(items: List<FlatPurchaseItem>) {
        adapter.updateList(items)

        val count = items.size
        binding.tvOrderCount.text = "$count pedido${if (count != 1) "s" else ""}"

        val empty = items.isEmpty()
        binding.rvPurchases.visibility = if(empty) View.GONE   else View.VISIBLE
        binding.llEmpty.visibility = if(empty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}