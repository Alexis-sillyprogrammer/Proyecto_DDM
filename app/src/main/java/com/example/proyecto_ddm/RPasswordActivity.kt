package com.example.proyecto_ddm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RPasswordActivity : AppCompatActivity() {

    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rpassword)

        userEmail = intent.getStringExtra("EMAIL") ?: ""

        val btnBack        = findViewById<ImageButton>(R.id.btnBack)
        val tvEmailInfo    = findViewById<TextView>(R.id.tvEmailInfo)
        val tilNewPassword = findViewById<TextInputLayout>(R.id.tilNewPassword)
        val tilConfirm     = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val etNewPassword  = findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirm      = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnSave        = findViewById<Button>(R.id.btnSave)

        tvEmailInfo.text = "Crea una nueva contraseña para $userEmail"

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirm     = etConfirm.text.toString().trim()

            tilNewPassword.error = null
            tilConfirm.error     = null

            if (newPassword.isEmpty()) {
                tilNewPassword.error = "Ingresa una contraseña"
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                tilNewPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            if (confirm.isEmpty()) {
                tilConfirm.error = "Confirma tu contraseña"
                return@setOnClickListener
            }

            if (newPassword != confirm) {
                tilConfirm.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            updatePassword(newPassword)
        }
    }

    private fun updatePassword(newPassword: String) {
        // Actualiza desde la BDD
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}