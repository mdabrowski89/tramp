package pl.mobite.tramp.ui.components.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_time_table_row.view.*
import pl.mobite.tramp.R
import pl.mobite.tramp.ui.models.TimeTableRow


class TimeTableAdapter: RecyclerView.Adapter<TimeTableViewHolder>() {

    private var items: List<TimeTableRow> = emptyList()

    fun setItems(newItems: List<TimeTableRow>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_table_row, parent, false)
        return TimeTableViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position % 2 == 1)
    }

}

class TimeTableViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val colorDarkViolet2 by lazy { ContextCompat.getColor(itemView.context, R.color.darkViolet2) }
    private val colorDarkViolet3 by lazy { ContextCompat.getColor(itemView.context, R.color.darkViolet3) }
    private val colorLightViolet2 by lazy { ContextCompat.getColor(itemView.context, R.color.lightViolet2) }
    private val colorLightViolet3 by lazy { ContextCompat.getColor(itemView.context, R.color.lightViolet3) }

    fun bind(timeTableRow: TimeTableRow, isOdd: Boolean) {
        with(itemView) {
            if (isOdd) {
                setBackgroundColor(colorDarkViolet3)
                rowMinutesLayout.setBackgroundColor(colorLightViolet3)
            } else {
                setBackgroundColor(colorDarkViolet2)
                rowMinutesLayout.setBackgroundColor(colorLightViolet2)
            }
            rowHour.text = timeTableRow.hour.toString()
            rowMinutesLayout.removeAllViews()
            timeTableRow.minutes.forEach { value ->
                val minuteTextView = TextView(ContextThemeWrapper(context, R.style.TimeTableRowText), null, 0)
                var minuteText = value.toString()
                if (minuteText.length == 1) {
                    minuteText = "0$minuteText"
                }
                minuteTextView.text = minuteText
                rowMinutesLayout.addView(minuteTextView)
            }
        }
    }
}