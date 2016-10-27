package dk.homburg.bluetoothtest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import dk.homburg.bluetoothtest.bluetooth.ConnectThread
import kotlinx.android.synthetic.main.activity_btdevice.*
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BTDeviceActivity : AppCompatActivity() {
    private val btAdapter: BluetoothAdapter by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private lateinit var device: BTDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btdevice)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val incomingDevice = intent.getParcelableExtra<BTDevice>("device")


        if (incomingDevice == null) {
            finish()
        } else {
            device = incomingDevice
            name.text = device.name
            address.text = device.address
            description.text = device.toString()
        }
    }

    fun onConnected(bytes: Int) {
        Timber.d("Connected!!!")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return true
    }

    inner class Connect(mBluetoothAdapter: BluetoothAdapter, mmDevice: BluetoothDevice) : ConnectThread(mBluetoothAdapter, mmDevice) {
        override fun manageConnectedSocket(socket: BluetoothSocket) {
            Timber.d("Connected socket: %s", socket)
            ConnectedThread(socket).run()
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream
        private val mmOutStream: OutputStream

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
                Timber.e(e)
            }

            mmInStream = tmpIn!!
            mmOutStream = tmpOut!!
        }

        override fun run() {
            val buffer = ByteArray(1024)  // buffer store for the stream
            var bytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer)
                    // Send the obtained bytes to the UI activity
                    runOnUiThread {
                        onConnected(bytes)
                    }
                } catch (e: IOException) {
                    Timber.e(e)
                    break
                }

            }
        }

        /* Call this from the main activity to send data to the remote device */
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Timber.e(e)
            }

        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Timber.e(e)
            }

        }
    }


    fun onConnect(view: View) {
        Timber.d("Connect!")
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val thread = Connect(bluetoothAdapter, device.device)
        thread.run()
    }
}
