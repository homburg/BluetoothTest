package dk.homburg.bluetoothtest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.roughike.bottombar.BottomBar
import dk.homburg.bluetoothtest.ui.TheAdapter
import kotlinx.android.synthetic.main.content_main.*
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import org.jetbrains.anko.intentFor
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    @PaperParcel
    data class LogItem(val date: String, val tag: String, val message: String)

    @PaperParcel
    data class State(val logList: List<LogItem>, val devices: Map<String, BTDevice>, val fragmentIdStack: List<Int>) : PaperParcelable {
        companion object {
            @JvmField val CREATOR = PaperParcelable.Creator(State::class.java)
        }

        val deviceList: List<BTDevice>
            get() = devices.values.sortedBy { it.address }
    }

    var state = State(emptyList<LogItem>(), emptyMap<String, BTDevice>(), emptyList())

    private var logItems = emptyList<LogItem>()
        set(value) {
            state = state.copy(logList = value)
            this.logAdapter.items = value
        }

    private fun currentDateStr(): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // Quoted "Z" to indicate UTC, no timezone offset
        df.timeZone = tz
        return df.format(Date())
    }


    val handler = Handler()
    val runnable = { Timber.d("Tick"); log("Tick", "tock"); scheduleReload() }

    val logAdapter = TheAdapter(R.layout.list_item, ::LogViewHolder)

    val deviceAdapter = TheAdapter(R.layout.list_item_bt_device) {
        DevicesViewHolder(it) {
            this@MainActivity.onBTDeviceSelected(it)
        }
    }

    private fun onBTDeviceSelected(btDevice: BTDevice) {
        Timber.d("Selected device: %s", btDevice)
        startActivity(intentFor<BTDeviceActivity>("device" to btDevice))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("Putting", state)
        outState.putParcelable("state", state)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.let {
            state = it.getParcelable<State>("state")
            Timber.d("Got items", state)
            logAdapter.items = state.logList
            deviceAdapter.items = state.deviceList
        }

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar?)

        // log("MainActivity", "onCreate")

        // title = "Log"

        registerReceiver(discoveryReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        val bottomBar = findViewById(R.id.bottomBar) as BottomBar
        bottomBar.setOnTabSelectListener {
            if (state.fragmentIdStack.lastOrNull() != it) {
                this.pushFragment(it)
            }
        }

        if (savedInstanceState == null) {
            logAdapter.items = emptyList()
            state = state.copy(fragmentIdStack = state.fragmentIdStack + R.id.tab_log)
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment, LogFragment())
                    .commit()
        }
    }

    private fun switchFragment(@IdRes id: Int) {
        when (id) {
            R.id.tab_devices -> switchFragment("Devices", DevicesFragment::class.java)
            else -> switchFragment("Log", LogFragment::class.java)
        }
    }

    private fun pushFragment(@IdRes id: Int) {
        Timber.d("Pushing %d", id)
        state = state.copy(fragmentIdStack = state.fragmentIdStack + id)
        switchFragment(id)
    }

    private fun popFragment(): Boolean {
        val end = state.fragmentIdStack.takeLast(2)
        if (end.size < 2) {
            return false
        } else {
            val previous = end.first()
            state = state.copy(fragmentIdStack = state.fragmentIdStack.dropLast(1))
            bottomBar.selectTabWithId(previous)
            switchFragment(previous)
            return true
        }
    }

    override fun onBackPressed() {
        Timber.d("Back pressed")
        if (!popFragment()) {
            super.onBackPressed()
        }
    }

    private fun switchFragment(newTitle: String, fragmentClass: Class<out Fragment>) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragmentClass.newInstance())
                .commit()
        title = newTitle
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.discover) {
            doSomeBluetooth()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private val REQUEST_ENABLE_BT: Int = 0x20
    private val REQUEST_PERMISSION = 0x40

    private fun doSomeBluetooth() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            log("BluetoothAdapter", "Null bt adapter")
            return
        }

        if (!btAdapter.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
            return
        }

        val pairedDevices = btAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            pairedDevices.forEach {
                log("Paired device", "[${it.name}] as (${it.address})")
            }
        } else {
            log("Paired devices", "none")
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                log("Coarse location permission", "We needs it, try again")
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION)
            }
            return
        }

        log("Discovery", "started ${btAdapter.startDiscovery()}")
    }

    private var devices: Map<String, BTDevice>
        set(value) {
            Timber.d("Setting devices: %s", value)
            state = state.copy(devices = value)
            deviceAdapter.items = state.deviceList
        }
        get() = state.devices

    private val discoveryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a ListView
                log("Discovery", "New device: [${device.name}] as (${device.address})")

                addDevice(device)
            }
        }
    }

    private fun addDevice(device: BluetoothDevice) {
        val newDevice = BTDevice.fromBluetoothDevice(device)
        devices += Pair(newDevice.address, newDevice)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT) {
            val tag = "Enable bluetooth"
            log(tag, "Got response")
            if (resultCode == RESULT_OK) {
                log(tag, "enabled!")
                doSomeBluetooth()
            } else {
                log(tag, "not!")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    log("Permission", "granted!")
                } else {
                    log("Permission", "not granted!")
                }
                doSomeBluetooth()
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun log(tag: String, message: String) {
        val list = this.logAdapter.items + LogItem(currentDateStr(), tag, message)
        this.logItems = list
        Timber.d("Items: %s", list)
    }

    private fun scheduleReload() {
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroy() {
        unregisterReceiver(discoveryReceiver)
        super.onDestroy()
    }
}

@PaperParcel
data class BTDevice(val name: String, val address: String) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(BTDevice::class.java)

        fun fromBluetoothDevice(device: BluetoothDevice): BTDevice {
            return BTDevice(
                    name = device.name ?: "",
                    address = device.address
            )
        }
    }
}
