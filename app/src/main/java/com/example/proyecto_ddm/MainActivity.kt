package com.example.proyecto_ddm

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.example.proyecto_ddm.databinding.ActivityMainBinding
import com.example.proyecto_ddm.fragments.AddProductFragment
import com.example.proyecto_ddm.fragments.CartFragment
import com.example.proyecto_ddm.fragments.CatalogFragment
import com.example.proyecto_ddm.fragments.ProfileFragment
import com.example.proyecto_ddm.fragments.PurchasesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isAdmin: Boolean = false
    var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        userId = intent.getIntExtra("USER_ID", -1)

        if(isAdmin) setupAdminNav()
        else setupClientNav()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    showNavBar()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun hideNavbar() {
        binding.bottomAppBar.visibility = View.GONE
        binding.fab.visibility = View.GONE
        binding.bottomNavigationClient.visibility = View.GONE
    }

    fun showNavBar() {
        if (isAdmin) {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
        } else binding.bottomNavigationClient.visibility = View.VISIBLE
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    fun navigateToDetail(fragment: Fragment) {
        hideNavbar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupAdminNav() {
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE
        binding.bottomNavigationClient.visibility = View.GONE

        binding.bottomNavigationLeft.background = null
        binding.bottomNavigationRight.background = null

        replaceFragment(CatalogFragment())
        binding.bottomNavigationLeft.selectedItemId = R.id.nav_catalog

        binding.bottomNavigationRight.menu.setGroupCheckable(0, true, false)
        binding.bottomNavigationRight.menu.forEach { it.isChecked = false }
        binding.bottomNavigationRight.menu.setGroupCheckable(0, true, true)

        binding.bottomNavigationLeft.setOnItemSelectedListener { item ->
            clearRightSelection()
            when (item.itemId) {
                R.id.nav_catalog -> replaceFragment(CatalogFragment())
                R.id.nav_cart -> replaceFragment(CartFragment())
            }
            true
        }

        binding.bottomNavigationRight.setOnItemSelectedListener { item ->
            clearLeftSelection()
            when (item.itemId) {
                R.id.nav_purchases -> replaceFragment(PurchasesFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }

        binding.fab.setOnClickListener {
            clearLeftSelection()
            clearRightSelection()
            replaceFragment(AddProductFragment())
        }
    }

    private fun setupClientNav() {
        binding.bottomNavigationClient.visibility = View.VISIBLE
        binding.bottomAppBar.visibility = View.GONE
        binding.fab.visibility = View.GONE

        replaceFragment(CatalogFragment())
        binding.bottomNavigationClient.selectedItemId = R.id.nav_catalog

        binding.bottomNavigationClient.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalog -> replaceFragment(CatalogFragment())
                R.id.nav_cart -> replaceFragment(CartFragment())
                R.id.nav_purchases -> replaceFragment(PurchasesFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun clearRightSelection() {
        binding.bottomNavigationRight.menu.setGroupCheckable(0, true, false)
        binding.bottomNavigationRight.menu.forEach { it.isChecked = false }
        binding.bottomNavigationRight.menu.setGroupCheckable(0, true, true)
    }

    private fun clearLeftSelection() {
        binding.bottomNavigationLeft.menu.setGroupCheckable(0, true, false)
        binding.bottomNavigationLeft.menu.forEach { it.isChecked = false }
        binding.bottomNavigationLeft.menu.setGroupCheckable(0, true, true)
    }
}