package com.university.gami_android.ui.write_review

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.university.gami_android.R
import com.university.gami_android.model.SendReview


class WriteReviewActivity : AppCompatActivity(), WriteReviewContract.View, View.OnClickListener {
    private lateinit var eventName: String
    private lateinit var presenter: WriteReviewPresenter

    private lateinit var backBtn: ImageView
    private lateinit var submitBtn: TextView

    private lateinit var rating: RatingBar
    private lateinit var reviewLayout: TextInputLayout
    private lateinit var reviewText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        eventName = intent.getStringExtra("EVENT_NAME")!!

        presenter = WriteReviewPresenter()
        presenter.bindView(this)

        initAttributes()
        setListeners()
    }

    private fun initAttributes() {
        backBtn = findViewById(R.id.back_btn)
        submitBtn = findViewById(R.id.submit_btn)
        rating = findViewById(R.id.new_rating)

        reviewLayout = findViewById(R.id.new_review)
        reviewText = findViewById(R.id.new_review_text)
    }

    private fun setListeners() {
        backBtn.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.back_btn -> navigateToEventDetailsActivity(appContext())

            R.id.submit_btn -> {
                if(hasErrorForReview()) {
                    presenter.doCreateReview(
                        eventName,
                        SendReview(
                            rating.rating,
                            reviewText.text.toString()
                        ), appContext())
                }
            }
        }
    }

    override fun hasErrorForReview() : Boolean {
        return if (!presenter.validateReview(reviewText.text.toString())) {
            reviewLayout.error = resources.getString(R.string.review_error)
            false
        } else {
            reviewLayout.error = null
            true
        }
    }

    override fun navigateToEventDetailsActivity(context: Context) {
        finish()
    }

    override fun appContext(): Context = applicationContext

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }
}