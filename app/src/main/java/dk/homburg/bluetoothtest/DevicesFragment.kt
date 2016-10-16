package dk.homburg.bluetoothtest

import dk.homburg.bluetoothtest.ui.RecyclerViewFragment
import dk.homburg.bluetoothtest.ui.TheAdapter

class DevicesFragment : RecyclerViewFragment<BTDevice>() {
    override fun adapterFromActivity(activity: MainActivity): TheAdapter<BTDevice> {
        return activity.deviceAdapter
    }
}
