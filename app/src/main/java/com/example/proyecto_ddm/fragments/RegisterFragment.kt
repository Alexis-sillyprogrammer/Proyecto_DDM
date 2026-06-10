package com.example.proyecto_ddm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        repo = GameVaultRepository(requireContext())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            if (validateFields()) register()
        }

        binding.tvGoToLogin.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateFields(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()
        var isValid = true

        if (name.length < 3) {
            binding.tilName.error = "Ingresa tu nombre completo"
            isValid = false
        } else binding.tilName.error = null

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Correo inválido"
            isValid = false
        } else binding.tilEmail.error = null

        if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else binding.tilPassword.error = null

        if (confirm != password) {
            binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
            isValid = false
        } else binding.tilConfirmPassword.error = null

        return isValid
    }

    private fun register() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        binding.btnRegister.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = repo.register(name, email, password)

                when {
                    result == -1L -> {
                        binding.tilEmail.error = "Este correo ya está registrado"
                    }
                    result == -2L -> {
                        Snackbar.make(
                            binding.root,
                            "Error al crear la cuenta. Inténtalo de nuevo.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        val intent = Intent(requireContext(), MainActivity::class.java).apply {
                            putExtra("IS_ADMIN", false)
                            putExtra("USER_ID", result.toInt())
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        startActivity(intent)
                        return@launch
                    }
                }

            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    "Error inesperado. Inténtalo de nuevo.",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                if(_binding != null) binding.btnRegister.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}