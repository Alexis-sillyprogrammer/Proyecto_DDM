package com.example.proyecto_ddm.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var isPhoto = false
    private val currentName = "Donnet Pitalua"
    private val currentEmail = "donnetpitalua127@gmail.com"
    private val currentRole = "Administrador"
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

        loadUserData()
        setupAvatarClick()
        setupNameListener()
        setupSave()
    }

    private fun loadUserData() {
        binding.avatarView.setName(currentName)
        binding.username.text = currentName
        binding.userFullName.setText(currentName)
        binding.userEmail.setText(currentEmail)
        binding.userRole.text = currentRole
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
        imageUri  = null
        imagePath = null
        isPhoto   = false

        binding.ivProfilePhoto.visibility = View.GONE
        binding.avatarView.visibility = View.VISIBLE
        binding.avatarView.setName(binding.userFullName.text.toString().ifEmpty { "Usuario" })
        binding.ivAddPhoto.apply {
            setImageResource(R.drawable.ic_outline_add_photo_24)
            setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            setBackgroundResource(R.drawable.circle_shape)
        }
    }

    private fun setupNameListener() {
        binding.userFullName.doAfterTextChanged { text ->
            val name = text.toString().trim()
            binding.username.text = name.ifEmpty { "Usuario" }

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
        val name = binding.userFullName.text.toString().trim()
        val email = binding.userEmail.text.toString().trim()

        if(name.isEmpty()) {
            binding.userFullName.error = "El nombre es obligatorio"
            return false
        }

        if(email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userEmail.error = "Ingresa un correo válido"
            return false
        }

        return true
    }

    private fun saveChanges() {
        val name = binding.userFullName.text.toString().trim()
        val email = binding.userEmail.text.toString().trim()

        if(imageUri != null)
            imagePath = copyImageAtStorage(imageUri!!)

        Snackbar.make(binding.root, "Perfil actualizado", Snackbar.LENGTH_SHORT).show()
    }

    private fun copyImageAtStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null

            val dir = File(requireContext().filesDir, "product_images").also { it.mkdirs() }
            val file = File(dir, "img_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { out ->
                inputStream.copyTo(out)
            }

            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}