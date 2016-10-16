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
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.roughike.bottombar.BottomBar
import dk.homburg.bluetoothtest.ui.TheAdapter
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    data class LogItem(val date: String, val tag: String, val message: String)

    private fun currentDateStr(): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // Quoted "Z" to indicate UTC, no timezone offset
        df.timeZone = tz
        return df.format(Date())
    }


    val handler = Handler()
    val runnable = { Timber.d("Tick"); log("Tick", "tock"); scheduleReload() }

    val logAdapter = TheAdapter<LogItem>(R.layout.list_item) { item, view ->
        (view.findViewById(R.id.logDate) as TextView).text = item.date
        (view.findViewById(R.id.logTag) as TextView).text = item.tag
        (view.findViewById(R.id.logMessage) as TextView).text = item.message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("On create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar?)


        log("MainActivity", "onCreate")

        // title = "Log"

        registerReceiver(discoveryReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        (findViewById(R.id.bottomBar) as BottomBar).setOnTabSelectListener {
            when (it) {
                R.id.tab_devices -> switchFragment("Devices", DevicesFragment::class.java)
                else -> switchFragment("Log", LogFragment::class.java)
            }
        }

        if (savedInstanceState == null) {
            //     supportFragmentManager.beginTransaction()
            //     .add(TestFragment(), "")
            //     .commit()
        }
    }

    private fun switchFragment(newTitle: String, fragmentClass: Class<out Fragment>) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragmentClass.newInstance())
                .addToBackStack(null)
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

    private val discoveryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a ListView
                log("Discovery", "New device: [${device.name}] as (${device.address})")
            }
        }
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
        this.logAdapter.items = list
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


