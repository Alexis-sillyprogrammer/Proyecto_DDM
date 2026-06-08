package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.AuthActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.databinding.FragmentFpasswordBinding

class FPasswordFragment : Fragment(R.layout.fragment_fpassword) {
    private var _binding: FragmentFpasswordBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFpasswordBinding.bind(view)

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
        (requireActivity() as AuthActivity).navigateTo(RPasswordFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}