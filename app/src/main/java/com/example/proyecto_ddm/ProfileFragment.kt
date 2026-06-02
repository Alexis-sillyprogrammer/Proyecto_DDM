package com.example.proyecto_ddm

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avatarView = view.findViewById<AlphabeticalAvatarView>(R.id.avatarView)
        val tvUserName = view.findViewById<TextView>(R.id.username)
        val etUserFullName = view.findViewById<EditText>(R.id.userFullName)
        val etEmail = view.findViewById<EditText>(R.id.userEmail)
        val tvRole = view.findViewById<TextView>(R.id.userRole)

        val userName = "Donnet Pitalua"
        avatarView.setName(userName)
        tvUserName.setText(userName)
        etUserFullName.setText(userName)
        etEmail.setText("donnetpitalua127@gmail.com")
        tvRole.setText("Admin")
    }
}