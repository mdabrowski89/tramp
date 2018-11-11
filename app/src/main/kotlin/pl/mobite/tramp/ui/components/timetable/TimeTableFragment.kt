package pl.mobite.tramp.ui.components.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.mobite.tramp.R
import pl.mobite.tramp.ui.base.BaseFragment


class TimeTableFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        return view
    }
}