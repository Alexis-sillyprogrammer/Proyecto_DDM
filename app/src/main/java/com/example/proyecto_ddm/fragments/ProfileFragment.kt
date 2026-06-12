package com.example.proyecto_ddm.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_ddm.AuthActivity
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.database.GameVaultRepository
import com.example.proyecto_ddm.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.edit

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: GameVaultRepository
    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var isPhoto = false
    private var userId: Int = -1
    private  val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK)
                result.data?.data?.let { uri ->
                    imageUri = uri
                    showProfilePhoto(uri)
                }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        repo = GameVaultRepository(requireContext())
        userId = requireActivity().intent.getIntExtra("USER_ID", -1)

        if(userId == -1) {
            Snackbar.make(binding.root, "Error al recuperar sesión de usuario", Snackbar.LENGTH_LONG).show()
            return
        }

        loadUserData()
        setupAvatarClick()
        setupNameListener()
        setupSave()
        setupLogout()
    }

    private fun loadUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = repo.getUserById(userId)
                if(user != null) {
                    binding.avatarView.setName(user.name)
                    binding.tvUsername.text = user.name
                    binding.etUserFullName.setText(user.name)
                    binding.etUserEmail.setText(user.email)
                    binding.tvUserRole.text = if(user.rol_id == 1) "Administrador" else "Usuario"

                    if(!user.img_path.isNullOrEmpty()) {
                        val imgFile = File(user.img_path)
                        if(imgFile.exists()) {
                            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                            if(bitmap != null) {
                                imagePath = user.img_path
                                renderBitmapToImageView(bitmap)
                            }
                        }
                    }

                    val stats = repo.getUserStats(userId)
                    binding.tvStatPurchases.text = stats.purchases.toString()
                    binding.tvStatCart.text = stats.cartItems.toString()
                    binding.tvStatFavorites.text = stats.favorites.toString()
                } else Snackbar.make(
                    binding.root,
                    "No se encontró el usuario",
                    Snackbar.LENGTH_SHORT
                ).show()
            } catch(e: Exception) {
                Snackbar.make(
                    binding.root,
                    "Error al cargar perfil desde la base de datos",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupAvatarClick() {
        binding.avatarView.setOnClickListener { showPhotoDialog() }
        binding.ivAddPhoto.setOnClickListener { showPhotoDialog() }
    }

    private fun showPhotoDialog() {
        val options = if(isPhoto) {
            arrayOf("Cambiar foto", "Eliminar foto", "Cancelar")
        } else {
            arrayOf("Seleccionar foto", "Cancelar")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(options) { _, which ->
                when(options[which]) {
                    "Seleccionar foto", "Cambiar foto" -> openGallery()
                    "Eliminar foto" -> deletePhoto()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }

    private fun showProfilePhoto(uri: Uri) {
        val bitmap = try {
            requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            null
        }

        if (bitmap == null) {
            Snackbar.make(binding.root, "No se pudo cargar la imagen", Snackbar.LENGTH_SHORT).show()
            return
        }

        renderBitmapToImageView(bitmap)
    }

    private fun renderBitmapToImageView(bitmap: Bitmap) {
        binding.avatarView.visibility = View.GONE
        binding.ivProfilePhoto.visibility = View.VISIBLE
        binding.ivProfilePhoto.setImageBitmap(bitmap)
        binding.ivProfilePhoto.apply {
            clipToOutline = true
            outlineProvider = object : android.view.ViewOutlineProvider() {
                override fun getOutline(view: View, outline: android.graphics.Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
        }

        binding.ivAddPhoto.apply {
            setImageResource(R.drawable.ic_outline_add_photo_24)
            setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            layoutParams = layoutParams.also {
                it.width  = 48.dp
                it.height = 48.dp
            }
        }

        isPhoto = true
    }

    private fun deletePhoto() {
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath!!)
            if (file.exists()) file.delete()
        }

        imageUri  = null
        imagePath = null
        isPhoto   = false

        binding.ivProfilePhoto.visibility = View.GONE
        binding.avatarView.visibility = View.VISIBLE
        binding.avatarView.setName(binding.etUserFullName.text.toString().ifEmpty { "Usuario" })
        binding.ivAddPhoto.apply {
            setImageResource(R.drawable.ic_outline_add_photo_24)
            setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            setBackgroundResource(R.drawable.circle_shape)
        }
    }

    private fun setupNameListener() {
        binding.etUserFullName.doAfterTextChanged { text ->
            val name = text.toString().trim()
            binding.tvUsername.text = name.ifEmpty { "Usuario" }

            if(!isPhoto) {
                binding.avatarView.setName(name.ifEmpty { "?" })
            }
        }
    }

    private fun setupSave() {
        binding.btnSaveProfile.setOnClickListener {
            if(validateFields()) saveChanges()
        }
    }

    private fun validateFields(): Boolean {
        val name = binding.etUserFullName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()

        if(name.isEmpty()) {
            binding.etUserFullName.error = "El nombre es obligatorio"
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etUserEmail.error = "Ingresa un correo válido"
            return false
        }

        return true
    }

    private fun saveChanges() {
        val name = binding.etUserFullName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        binding.btnSaveProfile.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if(imageUri != null) imagePath = copyImageAtStorage(imageUri!!)

                repo.updateProfile(userId, name, email, imagePath)
                binding.tvUsername.text = name
                if(!isPhoto) binding.avatarView.setName(name)
                Snackbar.make(
                    binding.root,
                    "Perfil actualizado correctamente",
                    Snackbar.LENGTH_SHORT
                ).show()
            } catch(e: Exception) {
                Snackbar.make(
                    binding.root,
                    "Error al guardar los cambios en la base de datos",
                    Snackbar.LENGTH_SHORT
                ).show()
            } finally {
                if(_binding != null) binding.btnSaveProfile.isEnabled = true
            }
        }
    }

    private fun copyImageAtStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null

            val dir = File(requireContext().filesDir, "product_images").also { it.mkdirs() }
            val file = File(dir, "profile_${userId}.jpg")

            FileOutputStream(file).use { out ->
                inputStream.copyTo(out)
            }

            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun setupLogout() {
        binding.btnLogOut.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("GameVaultPrefs", Context.MODE_PRIVATE)
            sharedPref.edit { clear() }

            val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
            requireActivity().finish()
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}