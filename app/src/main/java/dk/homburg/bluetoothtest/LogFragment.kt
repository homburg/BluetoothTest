package dk.homburg.bluetoothtest

import android.view.View
import android.widget.TextView
import dk.homburg.bluetoothtest.ui.RecyclerViewFragment
import dk.homburg.bluetoothtest.ui.TheAdapterViewHolder

class LogViewHolder(itemView: View) : TheAdapterViewHolder<MainActivity.LogItem>(itemView) {

    private var  logDate: TextView
    private var logTag: TextView
    private var logMessage: TextView

    init {
        logTag = itemView.findViewById(R.id.logTag) as TextView
        logDate = itemView.findViewById(R.id.logDate) as TextView
        logMessage = itemView.findViewById(R.id.logMessage) as TextView
    }

    override fun bind(item: MainActivity.LogItem) {
        logTag.text = item.tag
        logDate.text = item.date
        logMessage.text = item.message
    }

}

class LogFragment : RecyclerViewFragment<MainActivity.LogItem, LogViewHolder>() {
    override fun adapterFromActivity(activity: MainActivity) = activity.logAdapter
}
