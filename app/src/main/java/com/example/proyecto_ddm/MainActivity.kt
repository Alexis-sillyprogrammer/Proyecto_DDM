package com.example.proyecto_ddm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnGoLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoForgot).setOnClickListener {
            startActivity(Intent(this, FPasswordActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoReset).setOnClickListener {
            val intent = Intent(this, RPasswordActivity::class.java)
            intent.putExtra("EMAIL", "prueba@correo.com")
            startActivity(intent)
        }
    }
}