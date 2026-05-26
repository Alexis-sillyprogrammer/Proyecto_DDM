package com.example.proyecto_ddm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fpassword)

        val btnBack    = findViewById<ImageButton>(R.id.btnBack)
        val tilEmail   = findViewById<TextInputLayout>(R.id.tilEmail)
        val etEmail    = findViewById<TextInputEditText>(R.id.etEmail)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        btnBack.setOnClickListener { finish() }
        tvGoToLogin.setOnClickListener { finish() }

        btnContinue.setOnClickListener {
            val email = etEmail.text.toString().trim()
            tilEmail.error = null

            if (email.isEmpty()) {
                tilEmail.error = "Ingresa tu correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Correo no válido"
                return@setOnClickListener
            }

            checkEmailInDatabase(email)
        }
    }

    private fun checkEmailInDatabase(email: String) {
        // Consulta con la BD (exite el correo -> c: || no existe el correo -> :c)
        val intent = Intent(this, RPasswordActivity::class.java)
        intent.putExtra("EMAIL", email)
        startActivity(intent)
    }
}