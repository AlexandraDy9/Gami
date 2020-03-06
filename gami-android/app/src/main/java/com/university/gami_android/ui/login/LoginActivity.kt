package com.university.gami_android.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputEditText
import com.university.gami_android.R
import com.university.gami_android.ui.forgot_password.ForgotPasswordActivity
import com.university.gami_android.ui.main.MainActivity
import com.university.gami_android.ui.signUp.SignUpActivity
import com.university.gami_android.util.EditTextWatcher
import java.util.*

class LoginActivity : AppCompatActivity(), LoginContract.View, View.OnClickListener {

    private lateinit var presenter: LoginPresenter
    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var loginBtn: Button
    private lateinit var signUp: TextView
    private lateinit var forgotPass: TextView
    private lateinit var fbLogin: LoginButton
    private lateinit var callbackManager: CallbackManager
    private lateinit var progressBar: RelativeLayout

    private lateinit var loginData: LoginDao

    private val textWatcher = object : EditTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            validateInputs()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.login_button -> {
                presenter.doLogin(loginData, appContext())
            }

            R.id.sign_up -> {
                navigateToSignUpActivity(appContext())
            }

            R.id.forgot_pass -> {
                navigateToForgotPasswordActivity(appContext())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter()
        presenter.bindView(this)

        username = findViewById(R.id.username_text)
        username.addTextChangedListener(textWatcher)
        password = findViewById(R.id.password_text)
        password.addTextChangedListener(textWatcher)

        loginBtn = findViewById(R.id.login_button)
        signUp = findViewById(R.id.sign_up)
        forgotPass = findViewById(R.id.forgot_pass)

        validateInputs()

        loginBtn.setOnClickListener(this)
        signUp.setOnClickListener(this)
        forgotPass.setOnClickListener(this)
        progressBar = findViewById(R.id.progress_bar_login)

        fbLoginInit()
    }

    private fun fbLoginInit() {
        fbLogin = findViewById(R.id.fb_login)
        fbLogin.setReadPermissions(Arrays.asList("email", "public_profile", "user_birthday"))

        callbackManager = CallbackManager.Factory.create()
        fbLogin.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {}
                override fun onCancel() {}
                override fun onError(exception: FacebookException) {}
            })
    }

    fun validateInputs() {
        loginData = LoginDao(
            username.text.toString(),
            password.text.toString()
        )

        loginBtn.isEnabled = !presenter.validateInput(loginData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    var tokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
            if (currentAccessToken == null) {
                makeToast("Not logged in", appContext())
            } else {
                presenter.doFacebookLogin(currentAccessToken, appContext())
                progressBar.visibility = View.VISIBLE
            }
        }
    }


    override fun navigateToMainActivity(context: Context) {
        finish()
        startActivity(
            Intent(context, MainActivity::class.java)
        )
    }

    override fun navigateToSignUpActivity(context: Context) {
        startActivity(Intent(context, SignUpActivity::class.java))
    }

    override fun navigateToForgotPasswordActivity(context: Context) {
        startActivity(Intent(context, ForgotPasswordActivity::class.java))
    }

    override fun appContext(): Context {
        return applicationContext
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }
}