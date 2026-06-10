package com.example.proyecto_ddm.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.AuthActivity
import com.example.proyecto_ddm.MainActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        repo = GameVaultRepository(requireContext())

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
        } else binding.tilEmail.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else binding.tilPassword.error = null

        return isValid
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        binding.btnLogin.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = repo.login(email, password)

                if(user == null) {
                    binding.tilEmail.error = null
                    binding.tilPassword.error = "Correo o contraseña incorrectos"
                    return@launch
                }

                val sharedPref = requireContext().getSharedPreferences("GameVaultPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().apply {
                    putInt("PREF_USER_ID", user.user_id)
                    putBoolean("PREF_IS_ADMIN", user.rol_id == 1)
                    putBoolean("PREF_IS_LOGGED_IN", true)
                    apply()
                }

                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra("IS_ADMIN", user.rol_id == 1)
                    putExtra("USER_ID", user.user_id)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                startActivity(intent)
            } catch(e: Exception) {
                Snackbar.make(
                    binding.root,
                    "Error de conexión local. Reinicia la app.",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                if(_binding != null) binding.btnLogin.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}