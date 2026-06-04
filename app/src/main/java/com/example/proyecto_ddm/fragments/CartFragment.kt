package com.example.proyecto_ddm.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.CartAdapter
import com.example.proyecto_ddm.databinding.FragmentCartBinding
import com.example.proyecto_ddm.entities.CartItem
import com.example.proyecto_ddm.entities.Category
import com.example.proyecto_ddm.entities.Product
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var adapter: CartAdapter
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val exampleItems = mutableListOf(
        CartItem(
            product = Product(
                id = 1,
                name = "The Legend of Zelda",
                category = Category(1, "Videojuego"),
                description = "Aventura de acción en mundo abierto.",
                price = 1299.00f
            ),
            quantity = 1
        ),
        CartItem(
            product = Product(
                id = 2,
                name = "PlayStation 5",
                category = Category(2, "Consola"),
                description = "Consola de última generación de Sony.",
                price = 12999.00f
            ),
            quantity = 1
        ),
        CartItem(
            product = Product(
                id = 3,
                name = "Control DualSense",
                category = Category(3, "Accesorio"),
                description = "Control inalámbrico para PS5.",
                price = 1599.00f
            ),
            quantity = 2
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        setupRecyclerView()
        updateTotals()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            items = exampleItems,
            onQuantityChanged = { _, _ -> updateTotals() },
            onDeleteItem = { item -> confirmDeleteItem(item) }
        )

        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CartFragment.adapter
            setHasFixedSize(false)
        }

        updateEmptyState()
    }

    private fun setupButtons() {
        binding.btnPrcessOrder.setOnClickListener {
            if(adapter.getItems().isEmpty()) showSnackbar("Tu carrito está vacío")
            else showConfirmationAlert()
        }

        binding.btnDeleteCart.setOnClickListener {
            if(adapter.getItems().isEmpty()) showSnackbar("El carrito ya está vacío")
            else confirmEmptyCart()
        }
    }

    private fun showConfirmationAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar pedido")
            .setMessage("¿Estás seguro de que deseas confirmar tu pedido? A continuación elige la fecha y hora de recogida.")
            .setPositiveButton("Sí, continuar") { dialog, _ ->
                dialog.dismiss()
                showDatePicker()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val date = "$day/${month + 1}/$year"
                showTimePicker(date)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today.timeInMillis
            show()
        }
    }

    private fun showTimePicker(date: String) {
        val now = Calendar.getInstance()

        TimePickerDialog(
            requireContext(),
            { _, hour, minutes ->
                val hour12 = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }

                val amPm = if(hour >= 12) "PM" else "AM"
                val minFmt = String.format(Locale.US, "%02d", minutes)
                val hourFmt = "$hour12:$minFmt $amPm"

                processOrder(date, hourFmt)
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun processOrder(date: String, hour: String) {
        val dateHour = "$date a las $hour"

        adapter.emptyCart()
        updateTotals()
        updateEmptyState()

        AlertDialog.Builder(requireContext())
            .setTitle("¡Pedido confirmado!")
            .setMessage("Tu pedido será recogido el $dateHour.\n\nPuedes ver tu compra en «Compras».")
            .setPositiveButton("Ver mis compras") { dialog, _ ->
                dialog.dismiss()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, PurchasesFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmDeleteItem(item: CartItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Deseas quitar «${item.product.name}» del carrito?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                dialog.dismiss()
                adapter.deleteItem(item)
                updateTotals()
                updateEmptyState()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmEmptyCart() {
        AlertDialog.Builder(requireContext())
            .setTitle("Vaciar carrito")
            .setMessage("¿Estás seguro de que deseas eliminar todos los productos del carrito?")
            .setPositiveButton("Vaciar") { dialog, _ ->
                dialog.dismiss()
                adapter.emptyCart()
                updateTotals()
                updateEmptyState()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateTotals() {
        val subtotal = adapter.getItems().sumOf { it.subtotal.toDouble() }
        val iva = subtotal * 0.16
        val total = subtotal + iva

        binding.tvSubtotal.text = getString(R.string.currency_format, subtotal)
        binding.tvTax.text = getString(R.string.currency_format, iva)
        binding.tvTotal.text = getString(R.string.currency_format, total)
    }

    private fun updateEmptyState() {
        val empty = adapter.getItems().isEmpty()
        binding.rvCart.visibility = if (empty) View.GONE else View.VISIBLE
        binding.llEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        binding.btnPrcessOrder.isEnabled = !empty
        binding.btnDeleteCart.isEnabled = !empty
    }

    private fun showSnackbar(msg: String) {
        Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}