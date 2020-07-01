package com.university.gami_android.ui.add_event

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.ui.main.MainActivity
import com.university.gami_android.util.EditTextWatcher
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppCompatActivity(), View.OnClickListener, AddEventContract.View {
    private lateinit var backButton: ImageView

    private lateinit var location: EditText
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

    private lateinit var finishButton: Button

    private var lat: Double = 45.645713283056445
    private var long: Double = 25.591664314270023

    private var start: String = ""
    private var end: String = ""

    private var categoryList: MutableList<String> = mutableListOf()
    private var ages: MutableList<String> = mutableListOf()
    private var calendar: String? = null

    private lateinit var gc: Geocoder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gc = Geocoder(applicationContext)

        setContentView(R.layout.activity_add_event)
        presenter = AddEventPresenter()
        presenter.bindView(this)

        initAttributes()

        presenter.getCategories(this)
        presenter.getAgeRanges(this)
        initializeObservers()

        setListeners()
    }

    private fun initAttributes() {
        backButton = findViewById(R.id.back_btn)

        location = findViewById(R.id.event_location_text)
        name = findViewById(R.id.event_name_text)
        description = findViewById(R.id.event_description_text)
        numberOfAttendees = findViewById(R.id.nr_attendees_text)

        category = findViewById(R.id.event_category)
        ageRange = findViewById(R.id.age_range)

        date = findViewById(R.id.event_date_text)

        startTime = findViewById(R.id.start_time)
        endTime = findViewById(R.id.end_time)

        finishButton = findViewById(R.id.done_btn)
    }

    private fun setListeners() {
        backButton.setOnClickListener { finish() }
        date.setOnClickListener(this)
        finishButton.setOnClickListener(this)

        date.addTextChangedListener(object : EditTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                calendar = date.text.toString()
            }
        })

        startTime.setOnClickListener { setTime(startTime, true) }
        endTime.setOnClickListener { setTime(endTime, false) }
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

    private fun initializeCategories() {
        val categoryArrayAdapter = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            categoryList.toList()
        )
        category.adapter = categoryArrayAdapter

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
        ageRange.adapter = ageRangeArrayAdapter

        var splitingAgeRange: List<String>

        ageRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                splitingAgeRange = parent.getItemAtPosition(position).toString().split("-")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                makeToast(R.string.no_age_range.toString(), this@AddEventActivity)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.done_btn -> {
                if(location.text.toString().isNotBlank())  {
                    try{
                        val list = gc.getFromLocationName(location.text.toString(), 1)
                        val address: Address = list[0]
                        lat = address.latitude
                        long = address.longitude
                    }
                    catch (e: Exception) {
                        Log.w("error", e.message!!)
                    }
                }

                eventData = Event(
                    name.text.toString(),
                    description.text.toString(),
                    if (numberOfAttendees.text.toString() != "") numberOfAttendees.text.toString().toInt() else 0,
                    if(category.selectedItem != null) category.selectedItem.toString() else "",
                    long,
                    lat,
                    if(ageRange.selectedItem != null) ageRange.selectedItem.toString().split("-")[0].toInt() else 0,
                    if(ageRange.selectedItem != null) ageRange.selectedItem.toString().split("-")[1].toInt() else 0,
                    start,
                    end
                )
                if(!presenter.fieldsValidation(eventData, appContext())) {
                    presenter.addEvent(eventData, this)
                }
                else {
                    Toast.makeText(appContext(), R.string.invalid_data_add_event, Toast.LENGTH_LONG).show()
                }
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

                            setTime(startTime, true)
                            setTime(endTime, false)
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }
}