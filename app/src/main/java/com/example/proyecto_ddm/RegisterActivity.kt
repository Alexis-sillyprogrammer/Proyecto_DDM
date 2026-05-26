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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBack         = findViewById<ImageButton>(R.id.btnBack)
        val tilName         = findViewById<TextInputLayout>(R.id.tilName)
        val tilEmail        = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword     = findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirm      = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val etName          = findViewById<TextInputEditText>(R.id.etName)
        val etEmail         = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword      = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirm       = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegister     = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin     = findViewById<TextView>(R.id.tvGoToLogin)

        btnBack.setOnClickListener { finish() }
        tvGoToLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name     = etName.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm  = etConfirm.text.toString().trim()

            tilName.error    = null
            tilEmail.error   = null
            tilPassword.error = null
            tilConfirm.error  = null

            if (!validateFields(name, email, password, confirm, tilName, tilEmail, tilPassword, tilConfirm)) {
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }
    }

    //Cuando te equivocas en un campo, se marca como erroneo pero la marca está en el nombre
    //Al rato lo arreglo xd
    private fun validateFields(
        name: String, email: String, password: String, confirm: String,
        tilName: TextInputLayout, tilEmail: TextInputLayout, tilPassword: TextInputLayout, tilConfirm: TextInputLayout
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            tilName.error = "Ingresa tu nombre"
            isValid = false
        }

        if (email.isEmpty()) {
            tilName.error = "Ingresa tu correo"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tilEmail.error = "Correo no valido, verificalo"
            isValid = false
        }

        if (password.isEmpty()) {
            tilName.error = "Ingresa una contraseña"
            isValid = false
        } else if (password.length < 6 ) {
            tilPassword.error = "La contraseña debe tener minimo 6 caracteres"
            isValid = false
        }

        if (confirm.isEmpty()) {
            tilName.error = "Confirma tu contraseña"
            isValid = false
        } else if (password != confirm) {
            tilConfirm.error = "Las contraseñas no coinciden"
            isValid = false
        }
        return isValid
    }

    private fun registerUser(name: String, email: String, password: String) {
        //Conectar a BDD
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}