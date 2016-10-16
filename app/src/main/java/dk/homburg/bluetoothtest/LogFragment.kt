package dk.homburg.bluetoothtest

import dk.homburg.bluetoothtest.ui.RecyclerViewFragment
import dk.homburg.bluetoothtest.ui.TheAdapter

class LogFragment : RecyclerViewFragment<MainActivity.LogItem>() {
    override fun adapterFromActivity(activity: MainActivity): TheAdapter<MainActivity.LogItem> = activity.logAdapter
}
