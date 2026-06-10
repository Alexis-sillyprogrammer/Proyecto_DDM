package com.example.proyecto_ddm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.AuthActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentFpasswordBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FPasswordFragment : Fragment(R.layout.fragment_fpassword) {
    private var _binding: FragmentFpasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFpasswordBinding.bind(view)
        repo = GameVaultRepository(requireContext())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvGoToLogin.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnContinue.setOnClickListener {
            if (validateEmail()) sendRecovery()
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Ingresa un correo válido"
            false
        } else {
            binding.tilEmail.error = null
            true
        }
    }

    private fun sendRecovery() {
        val email = binding.etEmail.text.toString().trim()
        binding.btnContinue.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = repo.getUserByEmail(email)

                if(user == null) {
                    binding.tilEmail.error = "Correo no encontrado"
                    return@launch
                }

                binding.tilEmail.error = null
                val nextFragment = RPasswordFragment().apply {
                    arguments = Bundle().apply { putInt("USER_ID", user.user_id) }
                }

                (requireActivity() as AuthActivity).navigateTo(nextFragment)
            } catch(e: Exception) {
                Snackbar.make(
                    binding.root,
                    "Error al conectar con la base de datos. Inténtalo de nuevo.",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                if(_binding != null) binding.btnContinue.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}