package com.university.gami_android.ui.hobby_selection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.ui.login.LoginActivity


class HobbySelectionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var adapter: HobbySelectionAdapter

    private val selections = linkedSetOf<Int>()
    private lateinit var done: TextView
    private var counter: Int = 0

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.done -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun select(photo: Int) {
        selections += photo
    }

    private fun deselect(photo: Int) {
        selections -= photo
    }

    private fun updateViewAfterSelection(index: Int) {
        adapter.apply {
            selections = selections
            notifyItemChanged(index)
        }
        counter = selections.size
        done.text = "Done ($counter)"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_selection)

        val hobbies = listOf(
            Media(R.drawable.yoga, "Yoga"),
            Media(R.drawable.cooking, "Cooking"),
            Media(R.drawable.winter_sports, "Winter Sports"),
            Media(R.drawable.pottery, "Pottery"),
            Media(R.drawable.dance, "Dancing"),
            Media(R.drawable.music, "Music"),
            Media(R.drawable.painting, "Painting"),
            Media(R.drawable.swimming, "Swimming")
        )

        adapter = HobbySelectionAdapter(this)
        adapter.setMediaList(hobbies)

        val recyclerView = findViewById<RecyclerView>(R.id.hobbies_display)
        val numberOfColumns = 2
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        recyclerView.adapter = adapter

        adapter.selectMedia = { media, index ->
            val photo = media.photo
            val selected = photo in selections

            when {
                selected -> deselect(photo)
                !selected -> select(photo)
            }
            updateViewAfterSelection(index)

        }

        done = findViewById(R.id.done)
        done.text = "Done ($counter)"
        done.setOnClickListener(this)

    }

}