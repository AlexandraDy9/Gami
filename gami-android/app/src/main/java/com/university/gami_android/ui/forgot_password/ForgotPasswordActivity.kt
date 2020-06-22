package com.university.gami_android.ui.forgot_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.university.gami_android.R
import com.university.gami_android.model.SendMail
import com.university.gami_android.util.EditTextWatcher
import com.university.gami_android.util.getNavigationBarSize


class ForgotPasswordActivity : AppCompatActivity(), ForgotPasswordContract.View,
    View.OnClickListener {

    private lateinit var email: EditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var backButton: ImageView
    private lateinit var sendButton: Button
    private lateinit var progressBar: RelativeLayout

    private lateinit var presenter: ForgotPasswordPresenter

    private val textWatcher = object : EditTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            sendButton.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        presenter = ForgotPasswordPresenter()
        presenter.bindView(this)

        initAttribites()
        setListeners()
    }

    private fun initAttribites() {
        email = findViewById(R.id.email_text)
        emailLayout = findViewById(R.id.email)
        backButton = findViewById(R.id.back)
        sendButton = findViewById(R.id.send_btn)
        progressBar = findViewById(R.id.progress_bar)
        sendButton.isEnabled = false
    }

    private fun setListeners() {
        email.addTextChangedListener(textWatcher)
        backButton.setOnClickListener { finish() }
        sendButton.setOnClickListener(this)
    }

    override fun viewSnackbar() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            R.string.sent_email,
            Snackbar.LENGTH_LONG
        )
        snackbar.apply {
            setAction(R.string.go_to_email) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                startActivity(intent)
                startActivity(Intent.createChooser(intent, "Choose email"))
            }
            view.setPadding(0, 0, 0, getNavigationBarSize(resources))
            show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send_btn -> {
                if (hasErrorForEmail()) {
                    presenter.doForgotPassword(
                        SendMail(email.text.toString()),
                        appContext()
                    )
                    sendButton.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun changeProgressaBarVisibility(value: Boolean) {
        progressBar.visibility = if (!value) View.VISIBLE else View.INVISIBLE
    }

    private fun hasErrorForEmail(): Boolean {
        return if (!presenter.emailValidation(email.text.toString())) {
            emailLayout.error = resources.getString(R.string.email_error)
            false
        } else {
            emailLayout.error = null
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext
}