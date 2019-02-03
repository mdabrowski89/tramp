package pl.mobite.tramp.ui.components.timetable

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_time_table.*
import pl.mobite.tramp.R
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.base.MviBaseFragment
import pl.mobite.tramp.ui.components.MainActivity
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableAction
import pl.mobite.tramp.ui.components.timetable.mvi.TimeTableResult


class TimeTableFragment: MviBaseFragment<TimeTableAction, TimeTableResult, TimeTableViewState, TimeTableViewModel>(
    TimeTableViewModel::class.java
) {

    private val adapter = TimeTableAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).showDarkSystemUI()
    }

    override fun initialAction(state: TimeTableViewState?): TimeTableAction? {
        val args = TimeTableFragmentArgs.fromBundle(arguments)
        val timeTableDesc = TimeTableDesc(args.lineName, args.lineDirection, args.tramStopName, args.tramStopId)
        return TimeTableAction.GetTimeTableAction(timeTableDesc)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onApplyInsets(v: View, insets: WindowInsets): WindowInsets {
        if (!insets.isConsumed) {
            with(timeTableRecyclerView) {
                setPadding(paddingLeft, paddingTop, paddingRight, insets.systemWindowInsetBottom)
            }
            with(toolbar) {
                setPadding(paddingLeft, insets.systemWindowInsetTop, paddingRight, paddingBottom)
            }
        }
        return insets
    }

    private fun initRecyclerView() {
        timeTableRecyclerView.adapter = adapter
        timeTableRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    override fun render(state: TimeTableViewState) {
        with(state) {

            val showProgress = getTimeTableInProgress && timeTableRows.isNullOrEmpty()
            val showData = !timeTableRows.isNullOrEmpty()
            val showNoData = !getTimeTableInProgress && timeTableRows.isNullOrEmpty()

            progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
            timeTableRecyclerView.visibility = if (showData) View.VISIBLE else View.GONE
            noDataText.visibility = if (showNoData) View.VISIBLE else View.GONE

            if (showData && timeTableRows != null) {
                adapter.setItems(timeTableRows)
            }

            toolbar.setLineNumber(timeTableDetails?.lineName ?: "")
            tramStopNameText.text = timeTableDetails?.stopName ?: ""
            tramDirectionText.text = timeTableDetails?.lineDirection ?: ""

            getTimeTableError?.consume {
                Toast.makeText(requireContext(), R.string.time_table_fetch_error, Toast.LENGTH_LONG).show()
            }
        }
    }
}