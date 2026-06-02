package com.example.proyecto_ddm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.databinding.FragmentAddProductBinding
import com.google.android.material.snackbar.Snackbar

class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private var stockActual = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddProductBinding.bind(view)

        setupStock()
        setupButtons()
    }

    private fun setupStock() {
        binding.tvStock.text = stockActual.toString()

        binding.btnAdd.setOnClickListener {
            if(stockActual < 999) {
                stockActual++
                binding.tvStock.text = stockActual.toString()
            }
        }

        binding.btnSub.setOnClickListener {
            if(stockActual > 0) {
                stockActual--
                binding.tvStock.text = stockActual.toString()
            }
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            if(validateForm())
                saveProduct()
        }

        binding.btnCancel.setOnClickListener {
            cleanForm()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString().trim()
        when {
            name.isEmpty() -> {
                binding.tilName.error = "El nombre es obligatorio"
                isValid = false
            }
            name.length < 3 -> {
                binding.tilName.error = "El nombre debe tener al menos 3 caracteres"
                isValid = false
            }
            name.length > 100 -> {
                binding.tilName.error = "El nombre no puede exceder 100 caracteres"
                isValid = false
            }
            else -> binding.tilName.error = null
        }

        val description = binding.etDescription.text.toString().trim()
        when {
            description.isEmpty() -> {
                binding.tilDescription.error = "La descripción es obligatoria"
                isValid = false
            }
            description.length < 10 -> {
                binding.tilDescription.error = "La descripción debe tener al menos 10 caracteres"
                isValid = false
            }
            description.length > 500 -> {
                binding.tilDescription.error = "La descripción no puede exceder 500 caracteres"
                isValid = false
            }
            else -> binding.tilDescription.error = null
        }

        val priceText = binding.etPrice.text.toString().trim()
        when {
            priceText.isEmpty() -> {
                binding.tilPrice.error = "El precio es obligatorio"
                isValid = false
            }
            else -> {
                val price = priceText.toDoubleOrNull()
                when {
                    price == null -> {
                        binding.tilPrice.error = "Ingresa un precio válido"
                        isValid = false
                    }
                    price <= 0 -> {
                        binding.tilPrice.error = "El precio debe ser mayor a $0"
                        isValid = false
                    }
                    price > 99999.99 -> {
                        binding.tilPrice.error = "El precio no puede exceder $99,999.99"
                        isValid = false
                    }
                    else -> binding.tilPrice.error = null
                }
            }
        }

        val categoryId = binding.chipGroupCategory.checkedChipId
        if(categoryId == View.NO_ID) {
            showSnackbar("Selecciona una categoria")
            isValid = false
        }

        if(stockActual == 0)
            showSnackbar("El stock está en 0, el producto no será visible para clientes")

        return isValid
    }

    private fun saveProduct() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toDouble()

        showSnackbar("Producto \"$name\" guardado correctamente")
    }

    private fun getCategory(): String {
        return when(binding.chipGroupCategory.checkedChipId) {
            R.id.chipConsole -> "consola"
            R.id.chipAccessory -> "accesorio"
            else -> "videojuego"
        }
    }

    private fun cleanForm() {
        binding.etName.text?.clear()
        binding.etDescription.text?.clear()
        binding.etPrice.text?.clear()
        binding.tilName.error = null
        binding.tilDescription.error = null
        binding.tilPrice.error = null
        binding.chipGroupCategory.check(R.id.chipGame)
        stockActual = 1
        binding.tvStock.text = "1"
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}