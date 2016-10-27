package dk.homburg.bluetoothtest

import android.view.View
import android.widget.TextView
import dk.homburg.bluetoothtest.ui.RecyclerViewFragment
import dk.homburg.bluetoothtest.ui.TheAdapterViewHolder

class InputEventViewHolder(itemView: View) : TheAdapterViewHolder<InputEventActivity.InputEvent>(itemView) {

    private var  logDate: TextView
    private var logTag: TextView
    private var logMessage: TextView

    init {
        logTag = itemView.findViewById(R.id.logTag) as TextView
        logDate = itemView.findViewById(R.id.logDate) as TextView
        logMessage = itemView.findViewById(R.id.logMessage) as TextView
    }

    override fun bind(item: InputEventActivity.InputEvent) {
        logMessage.text = item.event.toString()
    }

}

class InputEventFragment : RecyclerViewFragment<InputEventActivity, InputEventActivity.InputEvent, InputEventViewHolder>() {
    override fun adapterFromActivity(activity: InputEventActivity) = activity.inputEventAdapter
}

