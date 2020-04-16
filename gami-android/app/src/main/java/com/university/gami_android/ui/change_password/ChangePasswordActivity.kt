package com.university.gami_android.ui.change_password

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.university.gami_android.R
import com.university.gami_android.model.ChangePassword
import com.university.gami_android.ui.login.LoginActivity
import com.university.gami_android.util.EditTextWatcher


class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View, View.OnClickListener {

    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var backButton: ImageView
    private lateinit var doneButton: Button
    private lateinit var data: Uri
    private lateinit var email: String

    private lateinit var presenter: ChangePasswordPresenter

    private val textWatcher = object : EditTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            doneButton.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        data = intent.data!!
        email = data.toString().substringAfterLast("/")

        presenter = ChangePasswordPresenter()
        presenter.bindView(this)

        initAttributes()
        setListeners()
    }

    private fun initAttributes() {
        password = findViewById(R.id.password_text)
        passwordLayout = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password_text)
        confirmPasswordLayout = findViewById(R.id.confirm_password)

        backButton = findViewById(R.id.back)
        doneButton = findViewById(R.id.done_btn)
        doneButton.isEnabled = false
    }

    private fun setListeners() {
        password.addTextChangedListener(textWatcher)
        confirmPassword.addTextChangedListener(textWatcher)
        backButton.setOnClickListener { finish() }
        doneButton.setOnClickListener(this)
    }

    override fun navigateToLoginActivity(context: Context) {
        finish()
        startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.done_btn -> {
                if (hasErrorForPassword() && hasErrorForConfirmPassword()) {
                    presenter.doChangePassword(
                        ChangePassword(email, password.text.toString()),
                        appContext()
                    )
                }
            }
        }
    }

    private fun hasErrorForPassword(): Boolean {
        return if (!presenter.passwordValidation(password.text.toString())) {
            passwordLayout.error = resources.getString(R.string.password_error)
            false
        } else {
            passwordLayout.error = null
            true
        }
    }

    private fun hasErrorForConfirmPassword(): Boolean {
        return if (!presenter.confirmPasswordValidation(
                password.text.toString(),
                confirmPassword.text.toString()
            )
        ) {
            confirmPasswordLayout.error = resources.getString(R.string.confirm_password_error)
            false
        } else {
            confirmPasswordLayout.error = null
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext
}