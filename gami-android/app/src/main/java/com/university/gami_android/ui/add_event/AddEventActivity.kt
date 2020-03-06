package com.university.gami_android.ui.add_event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.ui.main.MainActivity
import com.university.gami_android.util.EditTextWatcher
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppCompatActivity(), View.OnClickListener, AddEventContract.View {
    private lateinit var backButton: ImageView

    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var numberOfAttendees: EditText

    private lateinit var category: Spinner
    private lateinit var ageRange: Spinner

    private lateinit var date: EditText

    private var eventData: Event = Event()
    private lateinit var presenter: AddEventPresenter

    private lateinit var startTime: EditText
    private lateinit var endTime: EditText

    private lateinit var finishButton: TextView

    private var lat: Double = 0.0
    private var long: Double = 0.0

    private var start: String? = null
    private var end: String? = null

    private var categoryList: MutableList<String> = mutableListOf()
    private var ages: MutableList<String> = mutableListOf()
    private var calendar: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_event)
        presenter = AddEventPresenter()
        presenter.bindView(this)

        backButton = findViewById(R.id.back_btn)

        name = findViewById(R.id.event_name_text)
        description = findViewById(R.id.event_description_text)
        numberOfAttendees = findViewById(R.id.nr_attendees_text)

        category = findViewById(R.id.event_category)
        ageRange = findViewById(R.id.age_range)

        date = findViewById(R.id.event_date_text)

        startTime = findViewById(R.id.start_time)
        endTime = findViewById(R.id.end_time)

        finishButton = findViewById(R.id.done_btn)

        backButton.setOnClickListener { finish() }
        date.setOnClickListener(this)
        finishButton.setOnClickListener(this)

        presenter.getCategories(this)
        presenter.getAgeRanges(this)
        initializeObservers()

        name.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                eventData.name = name.text.toString()
            }
        })

        description.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                eventData.description = description.text.toString()
            }
        })

        numberOfAttendees.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                eventData.numberOfAttendees = numberOfAttendees.text.toString().toInt()
            }
        })

        date.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                calendar = date.text.toString()
            }
        })

        startTime.setOnClickListener {
            setTime(startTime, true)
        }

        endTime.setOnClickListener {
            setTime(endTime, false)
        }

        if (!Places.isInitialized()) {
            Places.initialize(this, R.string.places.toString())
        }

        var searchFragment =
            supportFragmentManager.findFragmentById(R.id.add_event_search) as AutocompleteSupportFragment
        var list = listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        searchFragment.setPlaceFields(list)
        searchFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                lat = place.latLng?.latitude!!
                long = place.latLng?.longitude!!
            }

            override fun onError(status: Status) {
                Toast.makeText(appContext(), "An error occurred: " + status, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun initializeObservers() {
        presenter.categories.observe(this,
            Observer { categ ->
                categ?.forEach { categoryList.add(it.name) }
                initializeCategories()
            })

        presenter.ageRanges.observe(this,
            Observer { range ->
                range?.forEach { ages.add(it.ageMin.toString() + "-" + it.ageMax.toString()) }
                initializeAgeRanges()
            })
    }

    private fun initializeCategories() {
        val categoryArrayAdapter =
            ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                categoryList.toList()
            )
        category.setAdapter(categoryArrayAdapter)

        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                eventData.categoryName = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                makeToast(R.string.no_category.toString(), this@AddEventActivity)
            }
        }
    }

    private fun initializeAgeRanges() {
        val ageRangeArrayAdapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ages)
        ageRange.setAdapter(ageRangeArrayAdapter)

        var splitingAgeRange: List<String>

        ageRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                splitingAgeRange = parent.getItemAtPosition(position).toString().split("-")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                makeToast(R.string.no_age_range.toString(), this@AddEventActivity)
            }
        }
    }

    private fun setTime(editText: EditText, isStart: Boolean) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val timePicker: TimePickerDialog
        timePicker = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
                if (isStart) {
                    start = calendar + "T" + editText.text.toString() + ":00"
                } else {
                    end = calendar + "T" + editText.text.toString() + ":00"
                }
            }, hour, minute, true
        )
        timePicker.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.done_btn -> {
                eventData = Event(
                    name.text.toString(),
                    description.text.toString(),
                    numberOfAttendees.text.toString().toInt(),
                    category.selectedItem.toString(),
//                    long!!,
//                    lat!!,
                    45.642109947940234,
                    25.58921813997586,
                    ageRange.selectedItem.toString().split("-")[0].toInt(),
                    ageRange.selectedItem.toString().split("-")[1].toInt(),
                    start!!,
                    end!!
                )
                presenter.addEvent(eventData, this)
                navigateToHomeActivity(this)
            }

            R.id.event_date_text -> {
                val calendar = Calendar.getInstance()
                val formatter = SimpleDateFormat(resources.getString(R.string.date_format))

                val datePickerDialog =
                    DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            val d: Calendar = Calendar.getInstance()
                            d.set(Calendar.YEAR, year)
                            d.set(Calendar.MONTH, monthOfYear)
                            d.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            date.setText(formatter.format(d.time))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                datePickerDialog.show()
            }
        }
    }

    override fun appContext(): Context = applicationContext

    override fun navigateToHomeActivity(context: Context) {
        finish()
        startActivity(Intent(context, MainActivity::class.java))
    }
}