package com.example.proyecto_ddm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.AuthActivity
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            if (validateFields()) login()
        }

        binding.btnRegister.setOnClickListener {
            (requireActivity() as AuthActivity).navigateTo(RegisterFragment())
        }

        binding.tvFPassword.setOnClickListener {
            (requireActivity() as AuthActivity).navigateTo(FPasswordFragment())
        }
    }

    private fun validateFields(): Boolean {
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        var isValid  = true

        if (email.isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Ingresa un correo válido"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isAdmin = email.contains("admin")
        navigateToMain(isAdmin)
    }

    private fun navigateToMain(isAdmin: Boolean) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("IS_ADMIN", isAdmin)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}