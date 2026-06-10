package com.example.proyecto_ddm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.fragments.LoginFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val sharedPref = getSharedPreferences("GameVaultPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("PREF_IS_LOGGED_IN", false)

        if(isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("USER_ID", sharedPref.getInt("PREF_USER_ID", -1))
                putExtra("IS_ADMIN", sharedPref.getBoolean("PREF_IS_ADMIN", false))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            return
        }

        if (savedInstanceState == null) {
            navigateTo(LoginFragment(), addToBackStack = false)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val tx = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.authContainer, fragment)

        if (addToBackStack) tx.addToBackStack(null)
        tx.commit()
    }
}