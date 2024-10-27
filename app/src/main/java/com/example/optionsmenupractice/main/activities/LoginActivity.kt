package com.example.optionsmenupractice.main.activities

import Database.DBHelper
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.optionsmenupractice.R
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var usernameTxt: EditText
    private lateinit var passwordTxt: EditText
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loginButton = findViewById(R.id.login_button)
        usernameTxt = findViewById(R.id.usernameTXT)
        passwordTxt = findViewById(R.id.passwordtxt)
        dbHelper = DBHelper(this) // Initialize DBHelper

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.registerNav -> {
                val register = Intent(this, RegisterActivity::class.java)
                startActivity(register)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loginUser() {
        val username = usernameTxt.text.toString()
        val password = passwordTxt.text.toString()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast("Please fill in login details")
        } else {
            val isValidUser = dbHelper.validateUser(username, password) // Validate user credentials

            if (isValidUser) {
                showToast("Login Successful")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Optionally finish LoginActivity
            } else {
                showErrorDialog("Login failed. Please check username and password.")
            }
        }
    }


    private fun showToast(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Login Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
