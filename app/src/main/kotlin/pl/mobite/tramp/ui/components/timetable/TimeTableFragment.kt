package pl.mobite.tramp.ui.components.timetable

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_time_table.*
import pl.mobite.tramp.R
import pl.mobite.tramp.ViewModelFactory
import pl.mobite.tramp.data.repositories.models.TimeTableDesc
import pl.mobite.tramp.ui.base.BaseFragment
import pl.mobite.tramp.ui.components.MainActivity


class TimeTableFragment: BaseFragment() {

    private val adapter = TimeTableAdapter()
    private val intentsRelay = PublishRelay.create<TimeTableIntent>()
    private var lastViewState: TimeTableViewState? = null
    private lateinit var viewModel: TimeTableViewModel
    private lateinit var disposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialViewState: TimeTableViewState? = savedInstanceState?.getParcelable(TimeTableViewState.PARCEL_KEY)
        viewModel = ViewModelProviders
            .of(this, ViewModelFactory.getInstance(initialViewState))
            .get(TimeTableViewModel::class.java)
    }

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

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states.subscribe(this::render))
        viewModel.processIntents(intentsRelay)

        val args = TimeTableFragmentArgs.fromBundle(arguments)
        val timeTableDesc = TimeTableDesc(args.lineName, args.lineDirection, args.tramStopName, args.tramStopId)
        intentsRelay.accept(TimeTableIntent.GetTimeTableIntent(timeTableDesc))
    }

    override fun onStop() {
        disposable.dispose()
        viewModel.dispose()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastViewState?.let { viewState ->
            outState.putParcelable(TimeTableViewState.PARCEL_KEY, viewState)
        }
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

    private fun render(state: TimeTableViewState) {
        with(state) {
            saveViewState(this)

            if (getTimeTableInProgress && (timeTableRows == null || timeTableRows.isEmpty())) {
                progressBar.visibility = View.VISIBLE
                timeTableRecyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                if (timeTableRows != null) {
                    timeTableRecyclerView.visibility = View.VISIBLE
                    adapter.setItems(timeTableRows)
                }
            }

            toolbar.setLineNumber(timeTableDetails?.lineName ?: "")
            tramStopNameText.text = timeTableDetails?.stopName ?: ""
            tramDirectionText.text = timeTableDetails?.lineDirection ?: ""

            if (getTimeTableError?.shouldBeDisplayed() == true) {
                Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveViewState(state: TimeTableViewState) {
        if (!state.getTimeTableInProgress) {
            lastViewState = state
        }
    }
}