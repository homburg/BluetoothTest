package dk.homburg.bluetoothtest

import android.view.View
import android.widget.TextView
import dk.homburg.bluetoothtest.ui.RecyclerViewFragment
import dk.homburg.bluetoothtest.ui.TheAdapter
import dk.homburg.bluetoothtest.ui.TheAdapterViewHolder

class DevicesViewHolder(itemView: View) : TheAdapterViewHolder<BTDevice>(itemView) {
    private val address: TextView
    private val name: TextView

    init {
        address = itemView.findViewById(R.id.deviceAddress) as TextView
        name = itemView.findViewById(R.id.deviceName) as TextView
    }

    override fun bind(item: BTDevice) {
        address.text = item.address
        name.text = item.name
    }
}

class DevicesFragment : RecyclerViewFragment<BTDevice, DevicesViewHolder>() {
    override fun adapterFromActivity(activity: MainActivity): TheAdapter<BTDevice, DevicesViewHolder> {
        return activity.deviceAdapter
    }
}
