package pl.mobite.tramp.ui.components.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_time_table.*
import pl.mobite.tramp.R
import pl.mobite.tramp.ui.base.BaseFragment


class TimeTableFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timetableIdText.text = TimeTableFragmentArgs.fromBundle(arguments).tramStopId
    }
}