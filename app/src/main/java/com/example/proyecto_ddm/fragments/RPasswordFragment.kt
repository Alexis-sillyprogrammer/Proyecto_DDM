package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentRpasswordBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RPasswordFragment : Fragment(R.layout.fragment_rpassword) {
    private var _binding: FragmentRpasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository
    private var userId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRpasswordBinding.bind(view)
        repo = GameVaultRepository(requireContext())
        userId = arguments?.getInt("USER_ID") ?: -1

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            if (validateFields()) savePassword()
        }
    }

    private fun validateFields(): Boolean {
        val password = binding.etNewPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()
        var isValid = true

        if (password.length < 6) {
            binding.tilNewPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else binding.tilNewPassword.error = null

        if (confirm != password) {
            binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
            isValid = false
        } else binding.tilConfirmPassword.error = null

        return isValid
    }

    private fun savePassword() {
        if(userId == -1) {
            Snackbar.make(binding.root, "Error de usuario. Inténtalo de nuevo.", Snackbar.LENGTH_LONG).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        val password = binding.etNewPassword.text.toString().trim()

        binding.btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repo.updatePasswordById(userId, password)
                Snackbar.make(
                    binding.root,
                    "Contraseña actualizada correctamente",
                    Snackbar.LENGTH_SHORT
                ).show()
                repeat(requireActivity().supportFragmentManager.backStackEntryCount) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            } catch(e: Exception) {
                Snackbar.make(binding.root, "Error al guardar en la base de datos", Snackbar.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}