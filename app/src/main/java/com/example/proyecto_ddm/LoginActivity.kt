package com.example.proyecto_ddm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private val adminAccounts = setOf(
        "a24110085@ceti.mx",
        /*
        correo de Donita y Alan (no me acuerdo xd)
         */
    )

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvFPassword = findViewById<TextView>(R.id.tvFPassword)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Son solo 2 campos... ¿acaso no ves?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvFPassword.setOnClickListener {
            val intent = Intent(this, FPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        val isAdmin = adminAccounts.contains(email.lowercase())

        if (isAdmin) {
            //intent al menú de administrador (products)
            //finish()
            Toast.makeText(this,"Hola Bom Día! ", Toast.LENGTH_SHORT).show()
        } else {
            //intent al menu de usuario (orders)
            //finish()
            Toast.makeText(this, "Bienvenido Random", Toast.LENGTH_SHORT).show()
        }
    }
}