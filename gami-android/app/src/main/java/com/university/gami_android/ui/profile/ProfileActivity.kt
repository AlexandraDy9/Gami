package com.university.gami_android.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import com.university.gami_android.R
import com.university.gami_android.model.Photo
import com.university.gami_android.model.User
import com.university.gami_android.model.UserDto
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.util.EditTextWatcher
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity(), ProfileContract.View, View.OnClickListener {
    private lateinit var email: TextView
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var birthday: EditText
    private lateinit var description: EditText
    private lateinit var backButton: ImageView
    private lateinit var editButton: Button
    private lateinit var profileImage: ImageView

    private lateinit var editData: UserDto
    private lateinit var presenter: ProfilePresenter
    private lateinit var carouselView: CarouselView
    private var imageList: MutableList<Bitmap> = mutableListOf()
    private var userPhotos: List<Photo>? = null

    private var imageListener: ImageListener = ImageListener { position, imageView ->
        imageView.setImageBitmap(
            imageList[position]
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        presenter = ProfilePresenter()
        presenter.bindView(this)

        initAttributes()
        setListeners()

        title = PreferenceHandler.getUserName()
        initObserves()
    }

    private fun initAttributes() {
        carouselView = findViewById(R.id.carousel_view)

        email = findViewById(R.id.email)
        backButton = findViewById(R.id.back)
        profileImage = findViewById(R.id.profile_image)
        editButton = findViewById(R.id.edit_btn)
        editButton.isEnabled = false

        firstName = findViewById(R.id.first_name_text)
        lastName = findViewById(R.id.last_name_text)
        birthday = findViewById(R.id.birthday_text)
        description = findViewById(R.id.description_text)

        editData = UserDto(
            firstName.text.toString(),
            lastName.text.toString(),
            birthday.text.toString(),
            description.text.toString(),
            ""
        )
    }

    private fun setListeners() {
        carouselView.apply {
            pageCount = imageList.size
            setImageListener(imageListener)
        }

        firstName.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                editData.firstName = firstName.text.toString()
                editButton.isEnabled = true
            }
        })

        lastName.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                editData.lastName = lastName.text.toString()
                editButton.isEnabled = true
            }
        })

        birthday.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                editData.birthday = birthday.text.toString()
                editButton.isEnabled = true
            }
        })

        description.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                editData.description = description.text.toString()
                editButton.isEnabled = true
            }
        })

        add_photo.setOnClickListener {
            getImageFromGallery()
        }

        birthday.setOnClickListener(this)
        backButton.setOnClickListener { finish() }
        editButton.setOnClickListener(this)
    }

    private fun initObserves() {
        presenter.getUser(appContext())
        presenter.userDetails.observe(this, Observer {
            firstName.setText(it?.firstname)
            lastName.setText(it?.lastname)
            email.text = it?.email
            birthday.setText(it?.birthdate)
            description.setText(it?.description)
        })

        presenter.getPhotos(appContext())
        presenter.userPhotos.observe(this, Observer { list ->
            userPhotos = list!!
            updatePhotos()
        })
    }

    @SuppressLint("SimpleDateFormat", "PrivateResource")
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.birthday_text -> {
                val calendar = Calendar.getInstance()
                val formatter = SimpleDateFormat(resources.getString(R.string.date_format))

                val datePickerDialog =
                    DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            val date: Calendar = Calendar.getInstance()
                            date.apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, monthOfYear)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            }

                            birthday.setText(formatter.format(date.time))

                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                datePickerDialog.show()
            }

            R.id.edit_btn -> {
                presenter.editUser(
                    User(
                        "",
                        "",
                        editData.firstName,
                        editData.lastName,
                        editData.description,
                        " ",
                        editData.birthday.toString()
                    ), appContext()
                )
            }
        }
    }

    private fun updatePhotos() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (userPhotos!!.isNotEmpty()) {
            profileImage.visibility = View.INVISIBLE
            carouselView.visibility = View.VISIBLE
            userPhotos?.forEach {
                val url = Uri.fromFile(File(it.image))
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, url)
                imageList.add(bitmap)

                val imageView = ImageView(this)
                Glide.with(this)
                    .load(it.image)
                    .into(imageView)

                imageListener.setImageForPosition(imageList.size - 1, imageView)
                carouselView.pageCount = carouselView.pageCount + 1
            }
        }
    }


    private fun getImageFromGallery() {
        val photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST)
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)

            val index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()

            return cursor!!.getString(index!!)
        } finally {
            cursor?.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                profileImage.visibility = View.INVISIBLE
                carouselView.visibility = View.VISIBLE
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageList.add(bitmap)

                val imageView = ImageView(this)
                Glide.with(this)
                    .load(bitmap)
                    .into(imageView)

                imageListener.setImageForPosition(imageList.size - 1, imageView)
                carouselView.pageCount = carouselView.pageCount + 1

                presenter.uploadUserPhoto(appContext(), getRealPathFromURI(appContext(), uri))

            } catch (e: IOException) {
                Log.e("ProfileActivity", e.message!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
    }
}
