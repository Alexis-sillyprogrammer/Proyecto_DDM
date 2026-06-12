package com.example.proyecto_ddm.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.CartAdapter
import com.example.proyecto_ddm.adapters.FavoriteAdapter
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentCartBinding
import com.example.proyecto_ddm.models.CartItem
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.Product
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class CartFragment : Fragment(R.layout.fragment_cart) {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var repo: GameVaultRepository
    private var userId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)
        repo = GameVaultRepository(requireContext())
        userId = requireActivity().intent.getIntExtra("USER_ID", -1)

        setupRecyclerView()
        setupFavorites()
        setupButtons()
        loadCart()
    }

    override fun onResume() {
        super.onResume()
        if(::cartAdapter.isInitialized) loadCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = mutableListOf(),
            onQuantityChanged = { item, newQty -> onQtyChanged(item, newQty) },
            onDeleteItem = { item -> confirmDeleteItem(item) }
        )

        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CartFragment.cartAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupFavorites() {
        favoriteAdapter = FavoriteAdapter(mutableListOf()) { product ->
            addFavoriteToCart(product)
        }

        binding.rvFavorites.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = favoriteAdapter
        }
    }

    private fun loadCart() {
        if (userId == -1) return

        viewLifecycleOwner.lifecycleScope.launch {
            val items = repo.getCartItems(userId)
            cartAdapter.updateList(items)
            updateTotals()
            updateEmptyState()
            loadFavorites()
        }
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            val favs = repo.getFavoriteProducts(userId)
            favoriteAdapter.updateList(favs)
            binding.cardFavorites.visibility = if (favs.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun addFavoriteToCart(product: Product) {
        viewLifecycleOwner.lifecycleScope.launch {
            val success = repo.addToCart(userId, product)
            if (success) {
                loadCart()
                showSnackbar("«${product.name}» agregado al carrito")
            }
        }
    }

    private fun onQtyChanged(item: CartItem, newQty: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            repo.updateCartItemQty(userId, item.product.id, newQty)
            updateTotals()
        }
    }

    private fun setupButtons() {
        binding.btnPrcessOrder.setOnClickListener {
            if(cartAdapter.getItems().isEmpty()) showSnackbar("Tu carrito está vacío")
            else showConfirmationAlert()
        }

        binding.btnDeleteCart.setOnClickListener {
            if(cartAdapter.getItems().isEmpty()) showSnackbar("El carrito ya está vacío")
            else confirmEmptyCart()
        }
    }

    private fun showConfirmationAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar pedido")
            .setMessage("¿Estás seguro? A continuación elige la fecha y hora de recogida.")
            .setPositiveButton("Sí, continuar") { d, _ -> d.dismiss(); showDatePicker() }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, year, month, day ->
                showTimePicker("$day/${month + 1}/$year")
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply { datePicker.minDate = today.timeInMillis; show() }
    }

    private fun showTimePicker(date: String) {
        val now = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, h, min ->
            val h12   = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
            val amPm  = if (h >= 12) "PM" else "AM"
            val fmt   = String.format(Locale.US, "%02d", min)
            processOrder(date, "$h12:$fmt $amPm")
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show()
    }

    private fun processOrder(date: String, hour: String) {
        val dateHour = "$date a las $hour"

        viewLifecycleOwner.lifecycleScope.launch {
            val success = repo.completeCart(userId, dateHour)

            if(!success) {
                showSnackbar("Error al procesar el pedido")
                return@launch
            }

            cartAdapter.emptyCart()
            updateTotals()
            updateEmptyState()

            AlertDialog.Builder(requireContext())
                .setTitle("¡Pedido confirmado!")
                .setMessage("Tu pedido será recogido el $dateHour.\n\nPuedes verlo en «Mis Compras».")
                .setPositiveButton("Ver mis compras") { d, _ ->
                    d.dismiss()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, PurchasesFragment())
                        .commit()
                }
                .setNegativeButton("Cerrar") { d, _ -> d.dismiss() }
                .show()
        }
    }

    private fun confirmDeleteItem(item: CartItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Quitar «${item.product.name}» del carrito?")
            .setPositiveButton("Eliminar") { d, _ ->
                d.dismiss()
                viewLifecycleOwner.lifecycleScope.launch {
                    repo.removeFromCart(userId, item.product.id)
                    cartAdapter.deleteItem(item)
                    updateTotals()
                    updateEmptyState()
                }
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .show()
    }

    private fun confirmEmptyCart() {
        AlertDialog.Builder(requireContext())
            .setTitle("Vaciar carrito")
            .setMessage("¿Eliminar todos los productos del carrito?")
            .setPositiveButton("Vaciar") { d, _ ->
                d.dismiss()
                viewLifecycleOwner.lifecycleScope.launch {
                    repo.clearCart(userId)
                    cartAdapter.emptyCart()
                    updateTotals()
                    updateEmptyState()
                }
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .show()

    }

    private fun updateTotals() {
        val subtotal = cartAdapter.getItems().sumOf { it.subtotal.toDouble() }
        val iva = subtotal * 0.16
        val total = subtotal + iva

        binding.tvSubtotal.text = getString(R.string.currency_format, subtotal)
        binding.tvTax.text = getString(R.string.currency_format, iva)
        binding.tvTotal.text = getString(R.string.currency_format, total)
    }

    private fun updateEmptyState() {
        val empty = cartAdapter.getItems().isEmpty()
        binding.rvCart.visibility = if (empty) View.GONE else View.VISIBLE
        binding.llEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        binding.btnPrcessOrder.isEnabled = !empty
        binding.btnDeleteCart.isEnabled = !empty
    }

    private fun showSnackbar(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}