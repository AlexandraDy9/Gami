package com.university.gami_android.ui.events

import android.os.Bundle
import android.view.View
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

class PlacesSearchFragment : AutocompleteSupportFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list: MutableList<Place.Field> = mutableListOf(Place.Field.ID, Place.Field.NAME)
        setPlaceFields(list)
    }
}
